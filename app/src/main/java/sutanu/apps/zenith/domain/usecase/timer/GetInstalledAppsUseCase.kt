package sutanu.apps.zenith.domain.usecase.timer

import sutanu.apps.zenith.domain.model.AppInfoUiState
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppTimerRepository
) {

    suspend operator fun invoke(): List<AppInfoUiState> = repository.fetchInstalledApps()
}