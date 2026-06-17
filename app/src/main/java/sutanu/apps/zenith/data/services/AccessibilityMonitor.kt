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
import javax.inject.Inject

@AndroidEntryPoint
class AccessibilityMonitor : AccessibilityService() {

    @Inject
    lateinit var appLimitDao: AppLimitDao

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            val openedPackageName = event.packageName?.toString() ?: return

            if (openedPackageName == packageName || openedPackageName.contains("launcher")) return

            Log.d("ZenithAccessibility", "Foreground package tracked: $openedPackageName")
            evaluateUsageQuotas(openedPackageName)
        }
    }

    private fun evaluateUsageQuotas(packageName: String) {
        serviceScope.launch(Dispatchers.IO) {
            val appLimitRecord = appLimitDao.getAppLimit(packageName)

            if (appLimitRecord != null) {
                if (appLimitRecord.isBlockedText) {
                    launch(Dispatchers.Main) {
                        launchBlockingOverlay(packageName, appLimitRecord.appName)
                    }
                }
            }
        }
    }

    private fun launchBlockingOverlay(packageName: String, appName: String) {
        Log.w("ZenithSecurity", "Enforcing hard wall on restricted package: $packageName")

        val overlayIntent = Intent().apply {
            setClassName(this@AccessibilityMonitor, "LockScreenOverlayActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("EXTRA_BLOCKED_PACKAGE", packageName)
            putExtra("EXTRA_BLOCKED_APP_NAME", appName)
        }

        try {
            startActivity(overlayIntent)
        } catch (e: Exception) {
            Log.e("ZenithSecurity", "Could not render structural barrier overlay window", e)
        }
    }

    override fun onInterrupt() {
        Log.e("ZenithAccessibility", "Accessibility tracking interrupted by Android system engine.")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}