package sutanu.apps.zenith.domain.model

data class RawAppLaunch(
    val packageName: String,
    val lastTimeUsedMs: Long,
    val totalUsageMinutesToday: Int
)
