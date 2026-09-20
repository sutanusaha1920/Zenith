package sutanu.apps.zenith.domain.usecase.bedtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

data class BedtimeConfig(
    val isEnabled: Boolean,
    val startTime: String,
    val endTime: String,
    val allowCalls: Boolean,
    val allowAlarms: Boolean,
    val allowWifi: Boolean
)

class GetBedtimeConfigurationUseCase @Inject constructor(
    private val repository: BedtimeRepository
) {

    operator fun invoke(): Flow<BedtimeConfig> {
        return combine(
            repository.isBedtimeEnabled,
            repository.bedtimeStartTime,
            repository.bedtimeEndTime,
            repository.bedtimeAllowCalls,
            repository.bedtimeAllowAlarms,
            repository.bedtimeAllowWifi
        ) { flowArray ->
            BedtimeConfig(
                isEnabled = flowArray[0] as Boolean,
                startTime = flowArray[1] as String,
                endTime = flowArray[2] as String,
                allowCalls = flowArray[3] as Boolean,
                allowAlarms = flowArray[4] as Boolean,
                allowWifi = flowArray[5] as Boolean
            )
        }
    }
}
