package sutanu.apps.zenith.domain.usecase.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.repository.AlertRepository
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class GetRecentAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository,
    private val appTimerRepository: AppTimerRepository,
    private val usageStatsRepository: UsageStatsRepository
) {

    operator fun invoke(): Flow<List<HomeAlert>> {
        return combine(
            alertRepository.getRecentAlerts(),
            appTimerRepository.getAppLimitsFlow(),
            tickerFlow
        ) { dbAlerts, limits, _ ->
            if (dbAlerts.isNotEmpty()) {
                dbAlerts
            } else {
                // Fallback: Compute live alerts on the fly if DB history is empty
                val packageNames = limits.map { it.packageName }
                val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)

                limits.mapNotNull { limitEntity ->
                    val currentUsage = usageMap[limitEntity.packageName] ?: 0
                    val dailyLimit = limitEntity.dailyLimitMinutes

                    if (dailyLimit <= 0) return@mapNotNull null

                    val severity = when {
                        currentUsage >= dailyLimit -> AlertSeverity.EXCEEDED
                        currentUsage >= (dailyLimit * 0.8) -> AlertSeverity.APPROACHING
                        else -> null
                    }

                    severity?.let {
                        HomeAlert(
                            packageName = limitEntity.packageName,
                            appName = limitEntity.appName,
                            alertType = it,
                            usage = formatMinutes(currentUsage),
                            limit = formatMinutes(dailyLimit),
                            timeStamp = "Just now"
                        )
                    }
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

    private val tickerFlow = flow {
        while (true) {
            emit(Unit)
            delay(60_000.milliseconds)
        }
    }
}
