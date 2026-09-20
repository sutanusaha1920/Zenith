package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface BedtimeRepository {

    val isBedtimeEnabled: Flow<Boolean>
    val bedtimeStartTime: Flow<String>
    val bedtimeEndTime: Flow<String>
    val bedtimeAllowCalls: Flow<Boolean>
    val bedtimeAllowAlarms: Flow<Boolean>
    val bedtimeAllowWifi: Flow<Boolean>

    suspend fun setBedtimeEnabled(enabled: Boolean)

    suspend fun updateScheduleWindow(startTime: String, endTime: String)

    suspend fun setBedtimeAllowCalls(allow: Boolean)

    suspend fun setBedtimeAllowAlarms(allow: Boolean)

    suspend fun setBedtimeAllowWifi(allow: Boolean)
}
