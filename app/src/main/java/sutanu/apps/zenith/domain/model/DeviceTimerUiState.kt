package sutanu.apps.zenith.domain.model

data class DeviceTimerUiState(
    val isTimerEnabled: Boolean = true,
    val totalTimeUsedMinutes: Int,
    val deviceLimitHours: Float
)
