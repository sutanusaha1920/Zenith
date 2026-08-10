package sutanu.apps.zenith.domain.usecase.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sutanu.apps.zenith.domain.model.BedtimeInfo
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class GetBedtimeInfoUseCase @Inject constructor(
    private val bedtimeRepository: BedtimeRepository
) {
    operator fun invoke(): Flow<BedtimeInfo> {
        return combine(
            bedtimeRepository.isBedtimeEnabled,
            bedtimeRepository.bedtimeStartTime,
            bedtimeRepository.bedtimeEndTime
        ) { isEnabled, startTime, endTime ->

            BedtimeInfo(
                bedtimeStatus = isEnabled,
                startTime = startTime,
                endTime = endTime
            )
        }
    }
}