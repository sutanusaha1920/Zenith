package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

interface BedtimeRepository {

    fun getBedtimeExceptionsFlow(): Flow<List<AppLimitEntity>>

    suspend fun setScheduleEnabled(enabled: Boolean)

    suspend fun updateScheduleTime(startTime: String, endTime: String)

    suspend fun removeException(packageName: String)
}