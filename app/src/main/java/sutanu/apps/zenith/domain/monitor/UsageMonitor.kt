package sutanu.apps.zenith.domain.monitor

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import sutanu.apps.zenith.MainActivity
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.repository.AlertRepository
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class UsageMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appTimerRepository: AppTimerRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val alertRepository: AlertRepository
) {
    suspend fun checkUsageAndTriggerAlerts() {
        val appLimits = appTimerRepository.getAppLimitsFlow().first()
        val packageNames = appLimits.map { it.packageName }
        val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)

        for (limitEntity in appLimits) {
            val currentUsage = usageMap[limitEntity.packageName] ?: 0
            val dailyLimit = limitEntity.dailyLimitMinutes

            if (dailyLimit <= 0) continue

            val severity = when {
                currentUsage >= dailyLimit -> AlertSeverity.EXCEEDED
                currentUsage >= dailyLimit * 0.8 -> AlertSeverity.APPROACHING
                else -> null
            }

            if (severity != null) {
                val hasAlerted = alertRepository.hasAlertedToday(limitEntity.packageName, severity)

                if (!hasAlerted) {
                    val newAlert = HomeAlert(
                        packageName = limitEntity.packageName,
                        appName = limitEntity.appName,
                        alertType = severity,
                        usage = formatMinutes(currentUsage),
                        limit = formatMinutes(dailyLimit)
                    )
                    alertRepository.insertAlert(newAlert)
                    sendAppLimitNotification(context, newAlert)
                }
            }
        }
    }

    private fun sendAppLimitNotification(context: Context, alert: HomeAlert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val channelId = "zenith_app_limit_alerts"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Limit Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts you when you approach or reach app screen time limits."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val (title, contentText) = when (alert.alertType) {
            AlertSeverity.APPROACHING -> Pair(
                "⚠️ Approaching Time Limit",
                "You have used ${alert.appName ?: "this app"} for ${alert.usage ?: ""} (80% of your ${alert.limit ?: ""} daily limit)."
            )
            AlertSeverity.EXCEEDED -> Pair(
                "🚫 Daily Time Limit Reached",
                "${alert.appName ?: "App"} time limit reached (${alert.limit ?: ""}). App is now restricted."
            )
            else -> Pair(
                "⚠️ App Time Limit Warning",
                "${alert.appName ?: "App"} screen time limit warning."
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.packageName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(alert.packageName.hashCode(), notification)
    }

    private fun formatMinutes(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${remainingMinutes}m"
        }
    }
}
