package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface DeviceTimerRepository {

    val deviceLimitFlow: Flow<Float>
    val isTimerEnabledFlow: Flow<Boolean>

    suspend fun saveDeviceLimit(hours: Float)

    suspend fun setTimerEnabled(enabled: Boolean)
}