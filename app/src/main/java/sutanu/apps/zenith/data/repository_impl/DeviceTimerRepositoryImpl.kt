package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.DeviceTimerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceTimerRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
) : DeviceTimerRepository{

    override val deviceLimitFlow: Flow<Float> = authPreferences.deviceScreenTimeLimit

    override val isTimerEnabledFlow: Flow<Boolean> = authPreferences.isDeviceAdminEnabled

    override suspend fun saveDeviceLimit(hours: Float) {
        authPreferences.setDeviceScreenTimeLimit(hours)
    }

    override suspend fun setTimerEnabled(enabled: Boolean) {
        authPreferences.setDeviceAdminEnabled(enabled)
    }

}