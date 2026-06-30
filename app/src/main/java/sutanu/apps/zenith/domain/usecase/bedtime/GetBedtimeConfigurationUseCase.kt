package sutanu.apps.zenith.domain.usecase.bedtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

data class BedtimeConfig(
    val isEnabled: Boolean,
    val startTime: String,
    val endTime: String
)

class GetBedtimeConfigurationUseCase @Inject constructor(
    private val repository: BedtimeRepository
) {

    operator fun invoke(): Flow<BedtimeConfig> {
        return combine(
            repository.isBedtimeEnabled,
            repository.bedtimeStartTime,
            repository.bedtimeEndTime
        ) { enabled, start, end ->
            BedtimeConfig(enabled, start, end)
        }
    }
}