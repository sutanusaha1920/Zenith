package sutanu.apps.zenith.domain.model

data class Bedtime(
    val isScheduleEnabled: Boolean = false,
    val startTime: String = "10:00 PM",
    val endTime: String = "07:00 AM",
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val allowPhoneCalls: Boolean = true,
    val allowAlarms: Boolean = true,
    val allowWifi: Boolean = true
)
