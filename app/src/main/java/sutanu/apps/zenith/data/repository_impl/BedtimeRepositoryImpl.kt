package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class BedtimeRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
) : BedtimeRepository {

    override val isBedtimeEnabled: Flow<Boolean> = authPreferences.isBedtimeEnabled
    override val bedtimeStartTime: Flow<String> = authPreferences.bedtimeStartTime
    override val bedtimeEndTime: Flow<String> = authPreferences.bedtimeEndTime

    override suspend fun setBedtimeEnabled(enabled: Boolean) {
        authPreferences.setBedtimeEnabled(enabled)
    }

    override suspend fun updateScheduleWindow(start: String, end: String) {
        authPreferences.saveBedTimeWindow(start, end)
    }

}