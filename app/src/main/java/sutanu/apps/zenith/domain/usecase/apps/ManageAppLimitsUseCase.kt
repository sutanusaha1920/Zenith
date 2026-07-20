package sutanu.apps.zenith.domain.usecase.apps

import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import javax.inject.Inject

class ManageAppLimitsUseCase @Inject constructor(
    private val repository: AppTimerRepository
) {
    fun executeGetLimits() = repository.getAppLimitsFlow()

    suspend fun executeSaveLimit(entity: AppLimitEntity) = repository.saveAppLimit(entity)

    suspend fun executeDeleteLimit(packageName: String) = repository.deleteAppLimit(packageName)
}