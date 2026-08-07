package sutanu.apps.zenith.domain.usecase.apps

import sutanu.apps.zenith.domain.model.AppInfo
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppTimerRepository
) {
    suspend operator fun invoke(): List<AppInfo> = repository.fetchInstalledApps()
}