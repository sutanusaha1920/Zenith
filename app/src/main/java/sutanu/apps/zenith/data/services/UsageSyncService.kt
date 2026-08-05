package sutanu.apps.zenith.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

@AndroidEntryPoint
class UsageSyncService : LifecycleService() {

    @Inject lateinit var appTimerRepository: AppTimerRepository
    @Inject lateinit var usageStatsRepository: UsageStatsRepository

    private var isSyncing = false
    private val CHANNEL_ID = "usage_sync_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Immediate call to startForeground
        startForeground(1, createNotification())

        if (!isSyncing) {
            isSyncing = true
            lifecycleScope.launch(Dispatchers.IO) {
                while (true) {
                    syncUsage()
                    delay(60000) // Sync every 60s
                }
            }
        }
        return START_STICKY
    }

    private suspend fun syncUsage() {
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
        val intent = Intent("sutanu.apps.zenith.ACTION_BLOCK_APP").apply {
            setPackage(this@UsageSyncService.packageName)
            putExtra("EXTRA_BLOCKED_PACKAGE", packageName)
            putExtra("EXTRA_BLOCKED_APP_NAME", appName)
        }
        sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID, 
                "App Timer Sync", 
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Zenith Protection Active")
            .setContentText("Monitoring app usage limits...")
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}
