package sutanu.apps.zenith.domain.model

enum class UsageSeverity { NORMAL, HIGH, EXCESSIVE }

data class DailyUsage(
    val dayLabel: String,
    val usageMinutes: Int,
    val usageSeverity: UsageSeverity
)
