package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface UsageStatsRepository {
    fun getTodayTotalUsageMinutes(): Flow<Int>
    fun hasUsageStatsPermission(): Boolean
    fun getAppsUsageMinutes(packageNames: List<String>): Map<String, Int>
    fun getForegroundApp(): String?
}