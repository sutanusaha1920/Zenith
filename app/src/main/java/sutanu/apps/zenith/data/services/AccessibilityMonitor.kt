package sutanu.apps.zenith.data.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import sutanu.apps.zenith.MainActivity
import sutanu.apps.zenith.core.util.BedtimeUtils
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class AccessibilityMonitor : AccessibilityService() {

    @Inject
    lateinit var appLimitDao: AppLimitDao
    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository
    @Inject
    lateinit var authPreferences: AuthPreferences

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // Thread-safe in-memory cache of app limits
    private val cachedAppLimits = ConcurrentHashMap<String, AppLimitEntity>()

    // Security & Bedtime Mode state cache
    private var isUninstallProtectionEnabled = true
    private var isBedtimeEnabled = false
    private var bedtimeStart = "22:00"
    private var bedtimeEnd = "07:00"
    private var allowCalls = true
    private var allowAlarms = true
    private var allowWifi = true

    override fun onCreate() {
        super.onCreate()
        Log.i("ZenithAccessibility", "Accessibility Service CREATED")
        observeAppLimitsCache()
        observeBedtimeConfigCache()
        observeUninstallProtection()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("ZenithAccessibility", "Accessibility Service CONNECTED & READY")
    }

    private fun observeAppLimitsCache() {
        serviceScope.launch(Dispatchers.IO) {
            appLimitDao.getAllAppLimitsFlow().collect { limits ->
                cachedAppLimits.clear()
                limits.forEach { limit ->
                    cachedAppLimits[limit.packageName] = limit
                }
            }
        }
    }

    private fun observeUninstallProtection() {
        serviceScope.launch(Dispatchers.IO) {
            authPreferences.isUninstallProtectionEnabled.collect { enabled ->
                isUninstallProtectionEnabled = enabled
            }
        }
    }

    private fun observeBedtimeConfigCache() {
        serviceScope.launch(Dispatchers.IO) {
            combine(
                authPreferences.isBedtimeEnabled,
                authPreferences.bedtimeStartTime,
                authPreferences.bedtimeEndTime,
                authPreferences.bedtimeAllowCalls,
                authPreferences.bedtimeAllowAlarms,
                authPreferences.bedtimeAllowWifi
            ) { flowArray ->
                isBedtimeEnabled = flowArray[0] as Boolean
                bedtimeStart = flowArray[1] as String
                bedtimeEnd = flowArray[2] as String
                allowCalls = flowArray[3] as Boolean
                allowAlarms = flowArray[4] as Boolean
                allowWifi = flowArray[5] as Boolean
            }.collect { }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Detect app openings, view clicks, & system window changes
        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            val openedPackageName = event.packageName?.toString() ?: return

            // Enforce App Deletion / Tamper Protection FIRST
            if (isUninstallProtectionEnabled && isUninstallOrSettingsAttempt(openedPackageName, event)) {
                Log.w("ZenithSecurity", "Intercepted uninstall/settings attempt on Zenith ($openedPackageName). Navigating to Deletion Protection screen.")
                launchDeletionProtectionScreen()
                return
            }

            // Don't block Zenith itself or the Home Launcher for regular app usage limits
            if (openedPackageName == packageName || openedPackageName.contains("launcher")) return

            // Enforce Bedtime Mode blocking
            if (isBedtimeEnabled && BedtimeUtils.isCurrentTimeInBedtimeWindow(bedtimeStart, bedtimeEnd)) {
                if (!BedtimeUtils.isAppAllowedDuringBedtime(openedPackageName, packageName, allowCalls, allowAlarms, allowWifi)) {
                    Log.w("ZenithSecurity", "Bedtime Mode Active: Blocking $openedPackageName")
                    launchBlockingOverlay(openedPackageName, "Bedtime Mode")
                    return
                }
            }

            // Instant in-memory cache lookup for App Limits
            val limitRecord = cachedAppLimits[openedPackageName] ?: return

            evaluateUsageQuotas(openedPackageName, limitRecord)
        }
    }

    private fun isUninstallOrSettingsAttempt(openedPackageName: String, event: AccessibilityEvent): Boolean {
        val pkg = openedPackageName.lowercase(Locale.getDefault())

        // Collect text content from event and accessibility node hierarchy
        val eventTexts = mutableListOf<String>()
        event.text.forEach { eventTexts.add(it.toString().lowercase(Locale.getDefault())) }
        
        val source = event.source
        if (source != null) {
            collectNodeTexts(source, eventTexts, maxDepth = 5)
        }

        val fullText = eventTexts.joinToString(" ")

        // Must explicitly mention Zenith or sutanu.apps.zenith
        val mentionsZenith = fullText.contains("zenith") || fullText.contains("sutanu.apps.zenith")
        if (!mentionsZenith) return false

        // Do NOT block when user is managing Accessibility Settings
        val isAccessibilitySettingsPage = fullText.contains("accessibility") || 
                fullText.contains("downloaded services") || 
                fullText.contains("installed services") || 
                fullText.contains("use zenith") || 
                fullText.contains("shortcut")

        if (isAccessibilitySettingsPage) {
            return false // Allow user to manage accessibility settings without false positive triggers
        }

        // Detect Uninstall / Force Stop / Clear Data / App Info attempts in Settings, Package Installer, Vending (Play Store), or Launcher
        val isPackageInstaller = pkg.contains("packageinstaller") || pkg.contains("vending")
        val isSettings = pkg == "com.android.settings"
        val isLauncher = pkg.contains("launcher")

        val hasUninstallAction = fullText.contains("uninstall") || 
                fullText.contains("force stop") || 
                fullText.contains("clear data") || 
                fullText.contains("clear storage") || 
                fullText.contains("delete app") || 
                fullText.contains("remove app")

        if ((isPackageInstaller || isSettings || isLauncher) && hasUninstallAction) {
            return true // BLOCK!
        }

        // Block access to Zenith's App Info page in Settings specifically
        if ((isPackageInstaller || isSettings) && (fullText.contains("app info") || fullText.contains("storage & cache") || fullText.contains("permissions"))) {
            return true // BLOCK!
        }

        return false
    }

    private fun collectNodeTexts(node: AccessibilityNodeInfo?, list: MutableList<String>, maxDepth: Int) {
        if (node == null || maxDepth <= 0) return
        node.text?.let { list.add(it.toString().lowercase(Locale.getDefault())) }
        node.contentDescription?.let { list.add(it.toString().lowercase(Locale.getDefault())) }
        for (i in 0 until node.childCount) {
            collectNodeTexts(node.getChild(i), list, maxDepth - 1)
        }
    }

    private fun evaluateUsageQuotas(packageName: String, limitRecord: AppLimitEntity) {
        if (limitRecord.isBlockedText) {
            Log.w("ZenithSecurity", "Enforcing hard barrier on $packageName")
            launchBlockingOverlay(packageName, limitRecord.appName)
            return
        }

        if (limitRecord.dailyLimitMinutes > 0) {
            serviceScope.launch(Dispatchers.IO) {
                val liveUsageMinutes = usageStatsRepository.getAppsUsageMinutes(listOf(packageName))[packageName] ?: 0
                if (liveUsageMinutes >= limitRecord.dailyLimitMinutes) {
                    launch(Dispatchers.Main) {
                        Log.w("ZenithSecurity", "Enforcing barrier on $packageName. Live Usage: $liveUsageMinutes, Limit: ${limitRecord.dailyLimitMinutes}")
                        launchBlockingOverlay(packageName, limitRecord.appName)
                    }
                }
            }
        }
    }

    private fun launchDeletionProtectionScreen() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("EXTRA_NAVIGATE_TO", "uninstall_pin_protection")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("ZenithSecurity", "Could not navigate to Deletion Protection screen", e)
        }
    }

    private fun launchBlockingOverlay(packageName: String, appName: String) {
        val overlayIntent = Intent(this, LockScreenOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("EXTRA_BLOCKED_PACKAGE", packageName)
            putExtra("EXTRA_BLOCKED_APP_NAME", appName)
        }

        try {
            startActivity(overlayIntent)
        } catch (e: Exception) {
            Log.e("ZenithSecurity", "Could not render overlay", e)
        }
    }

    override fun onInterrupt() {
        Log.e("ZenithAccessibility", "Accessibility tracking interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
