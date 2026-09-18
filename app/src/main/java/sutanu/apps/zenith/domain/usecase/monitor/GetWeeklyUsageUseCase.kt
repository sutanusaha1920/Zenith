package sutanu.apps.zenith.domain.usecase.monitor

import sutanu.apps.zenith.domain.model.DailyUsage
import sutanu.apps.zenith.domain.model.UsageSeverity
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetWeeklyUsageUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    suspend operator fun invoke(): List<DailyUsage> {
        val weeklyUsage = usageStatsRepository.getWeeklyUsageMinutes()

        return weeklyUsage.map { (dayLabel, totalMinutes) ->
            val usageSeverity = when {
                totalMinutes < 120 -> UsageSeverity.NORMAL
                totalMinutes <= 240 -> UsageSeverity.HIGH
                else -> UsageSeverity.EXCESSIVE
            }

            DailyUsage(
                dayLabel = dayLabel,
                usageMinutes = totalMinutes,
                usageSeverity = usageSeverity
            )
        }
    }
}