package sutanu.apps.zenith.domain.usecase.bedtime

import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class UpdateBedtimeScheduleUseCase @Inject constructor(
    private val repository: BedtimeRepository
) {

    suspend fun toggleSchedule(enabled: Boolean) =
        repository.setBedtimeEnabled(enabled)

    suspend fun changeInterval(startTime: String, endTime: String) =
        repository.updateScheduleWindow(startTime, endTime)

    suspend fun toggleAllowCalls(allow: Boolean) =
        repository.setBedtimeAllowCalls(allow)

    suspend fun toggleAllowAlarms(allow: Boolean) =
        repository.setBedtimeAllowAlarms(allow)

    suspend fun toggleAllowWifi(allow: Boolean) =
        repository.setBedtimeAllowWifi(allow)
}
