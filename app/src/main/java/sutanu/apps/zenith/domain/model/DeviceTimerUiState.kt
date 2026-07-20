package sutanu.apps.zenith.domain.model

data class DeviceTimerUiState(
    val isTimerEnabled: Boolean = true,
    val totalTimeUsedMinutes: Int = 0,
    val deviceLimitHours: Float = 0.0f
)
