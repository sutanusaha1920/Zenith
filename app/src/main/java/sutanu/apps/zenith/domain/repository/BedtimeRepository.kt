package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

interface BedtimeRepository {

    val isBedtimeEnabled: Flow<Boolean>
    val bedtimeStartTime: Flow<String>
    val bedtimeEndTime: Flow<String>

    suspend fun setBedtimeEnabled(enabled: Boolean)

    suspend fun updateScheduleWindow(startTime: String, endTime: String)

}