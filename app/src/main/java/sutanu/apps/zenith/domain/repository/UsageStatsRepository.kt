package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface UsageStatsRepository {
    fun getTodayTotalUsageMinutes(): Flow<Int>
    fun hasUsageStatsPermission(): Boolean
    suspend fun getAppsUsageMinutes(packageNames: List<String>): Map<String, Int>
    suspend fun getForegroundApp(): String?
}