package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class BedtimeRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
) : BedtimeRepository {

    override val isBedtimeEnabled: Flow<Boolean> = authPreferences.isBedtimeEnabled
    override val bedtimeStartTime: Flow<String> = authPreferences.bedtimeStartTime
    override val bedtimeEndTime: Flow<String> = authPreferences.bedtimeEndTime
    override val bedtimeAllowCalls: Flow<Boolean> = authPreferences.bedtimeAllowCalls
    override val bedtimeAllowAlarms: Flow<Boolean> = authPreferences.bedtimeAllowAlarms
    override val bedtimeAllowWifi: Flow<Boolean> = authPreferences.bedtimeAllowWifi

    override suspend fun setBedtimeEnabled(enabled: Boolean) {
        authPreferences.setBedtimeEnabled(enabled)
    }

    override suspend fun updateScheduleWindow(start: String, end: String) {
        authPreferences.saveBedTimeWindow(start, end)
    }

    override suspend fun setBedtimeAllowCalls(allow: Boolean) {
        authPreferences.setBedtimeAllowCalls(allow)
    }

    override suspend fun setBedtimeAllowAlarms(allow: Boolean) {
        authPreferences.setBedtimeAllowAlarms(allow)
    }

    override suspend fun setBedtimeAllowWifi(allow: Boolean) {
        authPreferences.setBedtimeAllowWifi(allow)
    }
}
