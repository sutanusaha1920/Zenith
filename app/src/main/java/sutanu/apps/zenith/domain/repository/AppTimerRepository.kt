package sutanu.apps.zenith.domain.repository


import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.model.AppInfoUiState

interface AppTimerRepository {

    fun getAppLimitsFlow(): Flow<List<AppLimitEntity>>

    suspend fun fetchInstalledApps(): List<AppInfoUiState>

    suspend fun saveAppLimit(entity: AppLimitEntity)

    suspend fun deleteAppLimit(packageName: String)
}