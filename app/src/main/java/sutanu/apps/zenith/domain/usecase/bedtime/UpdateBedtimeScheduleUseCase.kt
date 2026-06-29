package sutanu.apps.zenith.domain.usecase.bedtime

import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class UpdateBedtimeScheduleUseCase @Inject constructor(
    private val repository: BedtimeRepository
) {

    suspend fun toggleSchedule(enabled: Boolean) =
        repository.setScheduleEnabled(enabled)

    suspend fun changeInterval(startTime: String, endTime: String) =
        repository.updateScheduleTime(startTime, endTime)
}