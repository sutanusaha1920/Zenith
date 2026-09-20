package sutanu.apps.zenith.data.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class AccessibilityMonitor : AccessibilityService() {

    @Inject
    lateinit var appLimitDao: AppLimitDao
    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // Thread-safe in-memory cache of app limits to avoid Room DB queries on every window change event
    private val cachedAppLimits = ConcurrentHashMap<String, AppLimitEntity>()

    override fun onCreate() {
        super.onCreate()
        Log.i("ZenithAccessibility", "Accessibility Service CREATED")
        observeAppLimitsCache()
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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Detect app openings
        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            val openedPackageName = event.packageName?.toString() ?: return

            // Don't block Zenith or the Launcher
            if (openedPackageName == packageName || openedPackageName.contains("launcher")) return

            // Instant in-memory cache lookup
            val limitRecord = cachedAppLimits[openedPackageName] ?: return

            evaluateUsageQuotas(openedPackageName, limitRecord)
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
