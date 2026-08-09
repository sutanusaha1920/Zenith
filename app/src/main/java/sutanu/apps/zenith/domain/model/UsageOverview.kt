package sutanu.apps.zenith.domain.model

data class UsageOverview(
    val totalUsage: String,
    val currentLimit: String,
    val monitoredAppsCount: Int,
    val exceededLimitsCount: Int,
    val deviceLimit: String
)
