package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AlertHistoryEntity
import sutanu.apps.zenith.domain.model.HomeAlert

interface AlertRepository {

    fun getRecentAlerts(): Flow<List<HomeAlert>>

    suspend fun insertAlert(alert: HomeAlert)

    suspend fun deleteOldAlerts()
}