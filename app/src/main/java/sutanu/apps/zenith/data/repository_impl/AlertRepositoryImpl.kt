package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sutanu.apps.zenith.data.local.db.dao.AlertHistoryDao
import sutanu.apps.zenith.data.local.db.entity.AlertHistoryEntity
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.repository.AlertRepository
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val alertHistoryDao: AlertHistoryDao
) : AlertRepository {
    override fun getRecentAlerts(): Flow<List<HomeAlert>> {
        return alertHistoryDao.getRecentAlerts().map { entities ->
            entities.map { entity ->
                HomeAlert(
                    packageName = entity.packageName,
                    appName = entity.appName,
                    alertType = AlertSeverity.valueOf(entity.severity),
                    usage = entity.usageAtTrigger,
                    limit = entity.limitAtTrigger,
                    timeStamp = getRelativeTime(entity.timeStamp),
                )
            }
        }
    }

    override suspend fun insertAlert(alert: HomeAlert) {
        val entity = AlertHistoryEntity(
            packageName = alert.packageName ?: "",
            appName = alert.appName ?: "",
            severity = alert.alertType?.name ?: "APPROACHING",
            timeStamp = System.currentTimeMillis(),
            usageAtTrigger = alert.usage ?: "",
            limitAtTrigger = alert.limit ?: ""
        )
        alertHistoryDao.insertAlert(entity)
    }

    override suspend fun deleteOldAlerts() {
        alertHistoryDao.deleteOldAlert()
    }

    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minutes = diff / (1000 * 60)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            else -> "${minutes / 60} hours ago"
        }
    }
}