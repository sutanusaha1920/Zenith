package sutanu.apps.zenith.domain.usecase.bedtime

import sutanu.apps.zenith.domain.repository.BedtimeRepository
import javax.inject.Inject

class RemoveBedtimeExceptionUseCase @Inject constructor(
    private val repository: BedtimeRepository
) {

    suspend operator fun invoke(packageName: String) {
        if (packageName.isBlank()) return
        repository.removeException(packageName)
    }
}