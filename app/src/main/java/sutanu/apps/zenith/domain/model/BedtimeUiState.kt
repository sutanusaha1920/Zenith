package sutanu.apps.zenith.domain.model

data class BedtimeUiState(
    val isScheduleEnabled: Boolean = false,
    val startTime: String = "10:00 PM",
    val endTime: String = "7:00 AM",
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5)
)
