package sutanu.apps.zenith.data.repository_impl

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sutanu.apps.zenith.domain.model.RawAppLaunch
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

                val totalTimeMs = stats?.filter { it.firstTimeStamp >= startTime }
                    ?.sumOf { it.totalTimeInForeground } ?: 0L
                emit((totalTimeMs / 1000 / 60).toInt())
                delay(30000) // Poll every 30 seconds when permission is granted
            } else {
                emit(0)
                delay(1000) // Poll every 1 second when permission is missing so it detects grants instantly
            }
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

    override suspend fun getAppsUsageMinutes(packageNames: List<String>): Map<String, Int> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        val statsList = usageStatsManager.queryAndAggregateUsageStats(
            startTime,
            endTime
        )

        return packageNames.associateWith { pkg ->
            val usageMs = statsList[pkg]
            if (usageMs != null) {
                (usageMs.totalTimeInForeground / 1000 / 60).toInt()
            } else 0
        }
    }

    override suspend fun getForegroundApp(): String? {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()
        var lastApp: String? = null

        while (stats.hasNextEvent()) {
            stats.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastApp = event.packageName
            }
        }
        return lastApp
    }

    override suspend fun getWeeklyUsageMinutes(): List<Pair<String, Int>> {
        val dayPairs = mutableListOf<Pair<String, Int>>()
        val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 6 downTo 0) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dayStart = cal.timeInMillis

            val dayEnd = if (i == 0) System.currentTimeMillis() else dayStart + 86_400_000L - 1L

            val dayLabel = dayLabelFormat.format(cal.time)

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                dayStart,
                dayEnd
            )

            val totalTimeMs = stats?.filter { it.firstTimeStamp >= dayStart - 3_600_000L }
                ?.sumOf { it.totalTimeInForeground } ?: 0L

            val totalMinutes = (totalTimeMs / 1000 / 60).toInt()

            dayPairs.add(dayLabel to totalMinutes)
        }

        return dayPairs
        }

    override suspend fun getRecentAppLaunches(): List<RawAppLaunch> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTimeToday = calendar.timeInMillis
        val endTimeNow = System.currentTimeMillis()

        val lastLaunchMap = mutableMapOf<String, Long>()
        val events = usageStatsManager.queryEvents(startTimeToday, endTimeNow)
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val pkg = event.packageName
                if (!pkg.isNullOrEmpty()) {
                    lastLaunchMap[pkg] = event.timeStamp
                }
            }
        }

        val usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(startTimeToday, endTimeNow)

        return lastLaunchMap.entries
            .asSequence()
            .filter { (pkg, _) ->
                pkg != context.packageName && !pkg.contains("launcher")
            }
            .map { (pkg, lastTimeMs) ->
                val usageMs = usageStatsMap[pkg]?.totalTimeInForeground ?: 0L
                val totalMinutes = (usageMs / 1000 / 60).toInt()

                RawAppLaunch(
                    packageName = pkg,
                    lastTimeUsedMs = lastTimeMs,
                    totalUsageMinutesToday = totalMinutes
                )
            }
            .sortedByDescending { it.lastTimeUsedMs }
            .take(10)
            .toList()
    }
}