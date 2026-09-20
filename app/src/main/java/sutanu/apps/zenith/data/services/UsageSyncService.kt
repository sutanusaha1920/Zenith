package sutanu.apps.zenith.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.monitor.UsageMonitor
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.DeviceTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class UsageSyncService : LifecycleService() {

    @Inject lateinit var appTimerRepository: AppTimerRepository
    @Inject lateinit var usageStatsRepository: UsageStatsRepository
    @Inject lateinit var deviceTimerRepository: DeviceTimerRepository
    @Inject lateinit var usageMonitor: UsageMonitor

    private var syncJob: Job? = null
    private val channelId = "usage_sync_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, createNotification())
        }

        if (syncJob == null || syncJob?.isCancelled == true) {
            syncJob = lifecycleScope.launch(Dispatchers.IO) {
                while (isActive) {
                    try {
                        syncUsage()
                    } catch (e: Exception) {
                        Log.e("UsageSyncService", "Error during usage sync loop", e)
                    }
                    delay(60.seconds)
                }
            }
        }
        return START_STICKY
    }

    private suspend fun syncUsage() {
        usageMonitor.checkUsageAndTriggerAlerts()

        val isTimerEnabled = deviceTimerRepository.isTimerEnabledFlow.first()

        if (isTimerEnabled) {
            val deviceLimitHours = deviceTimerRepository.deviceLimitFlow.first()
            val totalTimeUsedMinutes = usageStatsRepository.getTodayTotalUsageMinutes().first()
            val deviceLimitMinutes = (deviceLimitHours * 60).toInt()

            if (deviceLimitMinutes in 1..totalTimeUsedMinutes) {
                Log.d("ZenithSync", "Device Limit Hit: $totalTimeUsedMinutes / $deviceLimitMinutes mins")
                val foregroundApp = usageStatsRepository.getForegroundApp() ?: packageName
                launchBlockingOverlay(foregroundApp, "Device")
                return
            }
        }

        val trackedApps = appTimerRepository.getAppLimitsFlow().first()
        if (trackedApps.isEmpty()) return

        val packageNames = trackedApps.map { it.packageName }
        val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)
        val foregroundApp = usageStatsRepository.getForegroundApp()

        usageMap.forEach { (pkg, minutes) ->
            val currentLimit = appTimerRepository.getAppLimitSync(pkg)

            if (currentLimit != null) {
                appTimerRepository.updateDailyAppUsage(pkg, minutes)

                if (currentLimit.dailyLimitMinutes in 1..minutes) {
                    Log.d("ZenithSync", "Limit Hit: $pkg ($minutes/${currentLimit.dailyLimitMinutes})")
                    if (foregroundApp == pkg) {
                        launchBlockingOverlay(pkg, currentLimit.appName)
                    }
                }
            }
        }
    }

    private fun launchBlockingOverlay(packageName: String, appName: String) {
        val intent = Intent(this, LockScreenOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("EXTRA_BLOCKED_PACKAGE", packageName)
            putExtra("EXTRA_BLOCKED_APP_NAME", appName)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("ZenithSync", "Could not render lock screen overlay", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId, 
                "App Timer Sync", 
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Zenith Protection Active")
            .setContentText("Monitoring app usage limits...")
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        syncJob?.cancel()
        syncJob = null
        super.onDestroy()
    }
}
