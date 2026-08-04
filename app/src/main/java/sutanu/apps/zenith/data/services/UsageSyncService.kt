package sutanu.apps.zenith.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
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
import sutanu.apps.zenith.presentation.lock.overlay.LockScreenOverlayActivity
import javax.inject.Inject

@AndroidEntryPoint
class UsageSyncService : LifecycleService() {

    @Inject lateinit var appTimerRepository: AppTimerRepository
    @Inject lateinit var usageStatsRepository: UsageStatsRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForeground(1, createNotification())

        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                syncUsage()
                delay(60000)
            }
        }
        return START_STICKY
    }

    private suspend fun syncUsage() {
        //Gets all apps currently tracking from DB
        val trackedApps = appTimerRepository.getAppLimitsFlow().first()
        val packageNames = trackedApps.map { it.packageName }

        //Get latest usage from system
        val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)

        //Update DB
        usageMap.forEach { (pkg, minutes) ->
            appTimerRepository.updateDailyAppUsage(pkg, minutes)

            val appLimit = trackedApps.find { it.packageName == pkg }
            if (appLimit != null && appLimit.dailyLimitMinutes > 0 && minutes >= appLimit.dailyLimitMinutes) {
                if (usageStatsRepository.getForegroundApp() == pkg) {
                    launchBlockingOverlay(pkg, appLimit.appName)
                }
            }
        }
    }

    private fun launchBlockingOverlay(packageName: String, appName: String) {
        val intent = Intent(this, LockScreenOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("EXTRA_BLOCKED_PACKAGE", packageName)
            putExtra("EXTRA_BLOCKED_APP_NAME", appName)
        }
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "usage_sync_channel"
        val channelName = "App Timer Sync"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Zenith is active")
            .setContentText("Monitoring app usage limits...")
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}