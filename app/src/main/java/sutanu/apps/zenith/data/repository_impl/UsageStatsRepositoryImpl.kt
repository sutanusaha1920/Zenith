package sutanu.apps.zenith.data.repository_impl

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageStatsRepository {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    override fun getTodayTotalUsageMinutes(): Flow<Int> = flow {
        while (true) {
            if (hasUsageStatsPermission()) {
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startTime = calendar.timeInMillis

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )

                val totalTimeMs = stats?.sumOf { it.totalTimeInForeground } ?: 0L
                emit((totalTimeMs / 1000 / 60).toInt())
            } else {
                emit(0)
            }
            delay(30000) // Update every 30 seconds
        }
    }

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}