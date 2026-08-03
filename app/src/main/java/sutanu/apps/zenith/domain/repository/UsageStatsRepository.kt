package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface UsageStatsRepository {
    fun getTodayTotalUsageMinutes(): Flow<Int>
    fun hasUsageStatsPermission(): Boolean
}