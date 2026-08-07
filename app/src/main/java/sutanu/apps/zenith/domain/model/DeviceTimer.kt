package sutanu.apps.zenith.domain.model

data class DeviceTimer(
    val isTimerEnabled: Boolean = true,
    val totalTimeUsedMinutes: Int = 0,
    val deviceLimitHours: Float = 1.0f,
    val needsUsagePermission: Boolean = false
)
