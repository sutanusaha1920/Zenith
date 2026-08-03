package sutanu.apps.zenith.domain.usecase.timer

import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetUsageStatsUseCase @Inject constructor(
    private val repository: UsageStatsRepository
) {
    fun getTodayUsage() = repository.getTodayTotalUsageMinutes()
    
    fun hasPermission() = repository.hasUsageStatsPermission()
}