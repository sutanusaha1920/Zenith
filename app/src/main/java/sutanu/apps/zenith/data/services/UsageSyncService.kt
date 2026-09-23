package sutanu.apps.zenith.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sutanu.apps.zenith.R
import sutanu.apps.zenith.core.util.BedtimeUtils
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.monitor.UsageMonitor
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.DeviceTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class UsageSyncService : Service() {

    @Inject
    lateinit var usageMonitor: UsageMonitor

    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository

    @Inject
    lateinit var appTimerRepository: AppTimerRepository

    @Inject
    lateinit var deviceTimerRepository: DeviceTimerRepository

    @Inject
    lateinit var authPreferences: AuthPreferences

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    companion object {
        private const val CHANNEL_ID = "zenith_usage_tracker_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            while (isActive) {
                try {
                    syncUsage()
                } catch (e: Exception) {
                    Log.e("UsageSyncService", "Error during usage sync loop", e)
                }
                delay(5.seconds)
            }
        }
        return START_STICKY
    }

    private suspend fun syncUsage() {
        usageMonitor.checkUsageAndTriggerAlerts()

        // Master Switch check: if OFF, skip device and app limit enforcement
        val isTimerEnabled = deviceTimerRepository.isTimerEnabledFlow.first()
        if (!isTimerEnabled) return

        // Check active Device PIN Extension
        val deviceOverrideExp = authPreferences.deviceOverrideExpirationTimestamp.first()
        val isDeviceOverrideActive = System.currentTimeMillis() < deviceOverrideExp

        // 1. Evaluate Bedtime Mode
        val isBedtimeEnabled = authPreferences.isBedtimeEnabled.first()
        if (isBedtimeEnabled) {
            val startTime = authPreferences.bedtimeStartTime.first()
            val endTime = authPreferences.bedtimeEndTime.first()

            if (BedtimeUtils.isCurrentTimeInBedtimeWindow(startTime, endTime)) {
                val allowCalls = authPreferences.bedtimeAllowCalls.first()
                val allowAlarms = authPreferences.bedtimeAllowAlarms.first()
                val allowWifi = authPreferences.bedtimeAllowWifi.first()

                val foregroundApp = usageStatsRepository.getForegroundApp()
                if (foregroundApp != null && !BedtimeUtils.isAppAllowedDuringBedtime(foregroundApp, packageName, allowCalls, allowAlarms, allowWifi)) {
                    Log.d("ZenithSync", "Bedtime Mode Active: Blocking foreground app $foregroundApp")
                    launchBlockingOverlay(foregroundApp, "Bedtime Mode")
                    return
                }
            }
        }

        // 2. Evaluate Device Screen Time Limit
        if (!isDeviceOverrideActive) {
            val deviceLimitHours = deviceTimerRepository.deviceLimitFlow.first()
            val totalTimeUsedMinutes = usageStatsRepository.getTodayTotalUsageMinutes().first()
            val deviceLimitMinutes = (deviceLimitHours * 60f).roundToInt()

            if (deviceLimitMinutes in 1..totalTimeUsedMinutes) {
                Log.d("ZenithSync", "Device Limit Hit: $totalTimeUsedMinutes / $deviceLimitMinutes mins")
                val foregroundApp = usageStatsRepository.getForegroundApp() ?: packageName
                launchBlockingOverlay(foregroundApp, "Device")
                return
            }
        }

        // 3. Evaluate Individual App Limits
        val trackedApps = appTimerRepository.getAppLimitsFlow().first()
        if (trackedApps.isEmpty()) return

        val packageNames = trackedApps.map { it.packageName }
        val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)
        val foregroundApp = usageStatsRepository.getForegroundApp()

        usageMap.forEach { (pkg, minutes) ->
            val currentLimit = appTimerRepository.getAppLimitSync(pkg)

            if (currentLimit != null) {
                appTimerRepository.updateDailyAppUsage(pkg, minutes)

                val isAppOverrideActive = System.currentTimeMillis() < currentLimit.overrideExpirationTimestamp

                if (!isAppOverrideActive && !isDeviceOverrideActive && currentLimit.dailyLimitMinutes in 1..minutes) {
                    Log.d("ZenithSync", "Limit Hit: $pkg ($minutes/${currentLimit.dailyLimitMinutes})")
                    if (foregroundApp == pkg) {
                        launchBlockingOverlay(pkg, currentLimit.appName)
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
            Log.e("UsageSyncService", "Could not render overlay", e)
        }
    }

    private fun startForegroundServiceNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zenith Screen Time Safeguard")
            .setContentText("Actively monitoring healthy digital habits...")
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Zenith Usage Tracker",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors app usage and screen time limits in real-time."
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
