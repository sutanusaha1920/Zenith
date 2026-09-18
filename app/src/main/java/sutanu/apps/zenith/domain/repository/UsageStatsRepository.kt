package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.domain.model.RawAppLaunch

interface UsageStatsRepository {
    fun getTodayTotalUsageMinutes(): Flow<Int>
    fun hasUsageStatsPermission(): Boolean
    suspend fun getAppsUsageMinutes(packageNames: List<String>): Map<String, Int>
    suspend fun getForegroundApp(): String?

    suspend fun getWeeklyUsageMinutes(): List<Pair<String, Int>>

    suspend fun getRecentAppLaunches(): List<RawAppLaunch>
}