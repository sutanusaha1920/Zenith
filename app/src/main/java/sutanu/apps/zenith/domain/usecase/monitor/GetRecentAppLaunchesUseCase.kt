package sutanu.apps.zenith.domain.usecase.monitor

import android.content.Context
import android.text.format.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sutanu.apps.zenith.domain.model.AppLaunchInfo
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.domain.repository.UsageStatsRepository
import javax.inject.Inject

class GetRecentAppLaunchesUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository,
    private val appTimerRepository: AppTimerRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Flow<List<AppLaunchInfo>> {
        return appTimerRepository.getAppLimitsFlow().map { limits ->
            val limitsMap = limits.associateBy { it.packageName }
            val recentRawLaunches = usageStatsRepository.getRecentAppLaunches()
            val pm = context.packageManager

            recentRawLaunches.map { raw ->
                val (appName, icon) = try {
                    val appInfo = pm.getApplicationInfo(raw.packageName, 0)
                    val name = pm.getApplicationLabel(appInfo).toString()
                    val drawable = pm.getApplicationIcon(appInfo)
                    Pair(name, drawable)
                } catch (e: Exception) {
                    Pair(raw.packageName, null)
                }

                val limitEntity = limitsMap[raw.packageName]
                val isExceeded = if (limitEntity != null) {
                    limitEntity.isBlockedText || (
                            limitEntity.dailyLimitMinutes > 0 && raw.totalUsageMinutesToday >= limitEntity.dailyLimitMinutes
                            )
                } else {
                    false
                }

                val relativeTime = DateUtils.getRelativeTimeSpanString(
                    raw.lastTimeUsedMs,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()

                AppLaunchInfo(
                    packageName = raw.packageName,
                    appName = appName,
                    icon = icon,
                    lastLaunchedText = relativeTime,
                    usageText = formatMinutes(raw.totalUsageMinutesToday),
                    isLimitExceeded = isExceeded
                )
            }
        }
    }

    private fun formatMinutes(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${remainingMinutes}m"
        }
    }
}