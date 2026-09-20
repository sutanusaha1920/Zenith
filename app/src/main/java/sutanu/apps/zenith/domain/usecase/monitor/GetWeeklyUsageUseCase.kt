package sutanu.apps.zenith.domain.usecase.monitor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sutanu.apps.zenith.domain.model.DailyUsage
import sutanu.apps.zenith.domain.model.UsageSeverity
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetWeeklyUsageUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    suspend operator fun invoke(): List<DailyUsage> = withContext(Dispatchers.IO) {
        val weeklyUsage = usageStatsRepository.getWeeklyUsageMinutes()

        weeklyUsage.map { (dayLabel, totalMinutes) ->
            val usageSeverity = when {
                totalMinutes < 240 -> UsageSeverity.NORMAL
                totalMinutes <= 360 -> UsageSeverity.HIGH
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
