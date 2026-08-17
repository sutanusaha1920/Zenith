package sutanu.apps.zenith.domain.monitor

import kotlinx.coroutines.flow.first
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.repository.AlertRepository
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class UsageMonitor @Inject constructor(
    private val appTimerRepository: AppTimerRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val alertRepository: AlertRepository
) {
    suspend fun checkUsageAndTriggerAlerts() {
        val appLimits = appTimerRepository.getAppLimitsFlow().first()
        val packageNames = appLimits.map { it.packageName }
        val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)

        for (limitEntity in appLimits) {
            val  currentUsage = usageMap[limitEntity.packageName] ?: 0
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
                }
            }
        }
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