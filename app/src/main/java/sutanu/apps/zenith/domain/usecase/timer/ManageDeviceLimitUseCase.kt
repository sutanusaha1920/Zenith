package sutanu.apps.zenith.domain.usecase.timer

import sutanu.apps.zenith.domain.repository.DeviceTimerRepository
import javax.inject.Inject

class ManageDeviceLimitUseCase @Inject constructor(
    private val repository: DeviceTimerRepository
) {

    fun getLimit() = repository.deviceLimitFlow

    fun isEnabled() = repository.isTimerEnabledFlow

    suspend fun updateLimit(hours: Float) = repository.saveDeviceLimit(hours)

    suspend fun toggleTimer(enabled: Boolean) = repository.setTimerEnabled(enabled)
}