package sutanu.apps.zenith.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sutanu.apps.zenith.domain.model.UsageOverview
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.DeviceTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject
import kotlin.math.roundToInt

class GetHomeOverViewUseCase @Inject constructor(
    private val appTimerRepository: AppTimerRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val deviceTimerRepository: DeviceTimerRepository
) {
    operator fun invoke(): Flow<UsageOverview> {
        return combine(
            appTimerRepository.getAppLimitsFlow(),
            usageStatsRepository.getTodayTotalUsageMinutes(),
            deviceTimerRepository.deviceLimitFlow,
        ) { limits, totalUsage, deviceLimit ->

            val packageNames = limits.map { it.packageName }
            val usageMap = usageStatsRepository.getAppsUsageMinutes(packageNames)

            val exceededLimitCount = limits.count { limitEntity ->
                val currencyUsage = usageMap[limitEntity.packageName] ?: 0
                currencyUsage >= limitEntity.dailyLimitMinutes
            }

            val deviceLimitMinutes = (deviceLimit * 60f).roundToInt()
            val isDeviceLimitExceeded = deviceLimitMinutes in 1..totalUsage

            UsageOverview(
                totalUsage = formatMinutes(totalUsage),
                monitoredAppsCount = limits.size,
                exceededLimitsCount = exceededLimitCount,
                currentLimit = formatHours(deviceLimit),
                deviceLimit = formatHours(deviceLimit),
                isDeviceLimitExceeded = isDeviceLimitExceeded
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    val hour = minutes / 60
    val remainingMinutes = minutes % 60

    return if (hour > 0) {
        "${hour}h ${remainingMinutes}m"
    } else {
        "${remainingMinutes}m"
    }
}

private fun formatHours(hours: Float): String {
    return if (hours % 1 == 0f) {
        "${hours.toInt()}h"
    }
    else {
        "${hours}h"
    }
}
