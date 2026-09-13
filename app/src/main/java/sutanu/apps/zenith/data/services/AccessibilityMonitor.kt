package sutanu.apps.zenith.data.services

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import javax.inject.Inject

@AndroidEntryPoint
class AccessibilityMonitor : AccessibilityService() {

    @Inject
    lateinit var appLimitDao: AppLimitDao
    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val blockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("ZenithSecurity", "Broadcast received! Action: ${intent?.action}")
            if (intent?.action == "sutanu.apps.zenith.ACTION_BLOCK_APP") {
                val pkg = intent.getStringExtra("EXTRA_BLOCKED_PACKAGE") ?: return
                val name = intent.getStringExtra("EXTRA_BLOCKED_APP_NAME") ?: return
                Log.d("ZenithSecurity", "Manual block triggered for: $name")
                launchBlockingOverlay(pkg, name)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("ZenithAccessibility", "Accessibility Service CREATED")
        val filter = IntentFilter("sutanu.apps.zenith.ACTION_BLOCK_APP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(blockReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(blockReceiver, filter)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("ZenithAccessibility", "Accessibility Service CONNECTED & READY")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Detect app openings
        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            val openedPackageName = event.packageName?.toString() ?: return

            // Don't block Zenith or the Launcher
            if (openedPackageName == packageName || openedPackageName.contains("launcher")) return

            evaluateUsageQuotas(openedPackageName)
        }
    }

    private fun evaluateUsageQuotas(packageName: String) {
        serviceScope.launch(Dispatchers.IO) {
            val appLimitRecord = appLimitDao.getAppLimit(packageName)

            if (appLimitRecord != null) {

                val isHardBlocked = appLimitRecord.isBlockedText

                val liveUsageMinutes = usageStatsRepository.getAppsUsageMinutes(listOf(packageName))[packageName] ?: 0

                val isTimeLimitExceeded = appLimitRecord.dailyLimitMinutes in 1..liveUsageMinutes

                if (isHardBlocked || isTimeLimitExceeded) {
                    launch(Dispatchers.Main) {
                        Log.w("ZenithSecurity", "Enforcing barrier on $packageName. Live Usage: $liveUsageMinutes, Limit: ${appLimitRecord.dailyLimitMinutes}")
                        launchBlockingOverlay(packageName, appLimitRecord.appName)
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
        try {
            unregisterReceiver(blockReceiver)
        } catch (e: Exception) {}
        serviceJob.cancel()
    }
}
