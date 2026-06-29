package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class BedtimeRepositoryImpl @Inject constructor(
    private val appLimitDao: AppLimitDao,
    private val authPreferences: AuthPreferences
) : BedtimeRepository{

    override fun getBedtimeExceptionsFlow(): Flow<List<AppLimitEntity>> {
        return flow {
            emit(emptyList())
        }
    }

    override suspend fun setScheduleEnabled(enabled: Boolean) {
        authPreferences.setDeviceAdminEnabled(enabled)
    }

    override suspend fun updateScheduleTime(startTime: String, endTime: String) {

    }

    override suspend fun removeException(packageName: String) {

    }

}