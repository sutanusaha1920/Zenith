package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sutanu.apps.zenith.data.local.db.dao.AlertHistoryDao
import sutanu.apps.zenith.data.local.db.entity.AlertHistoryEntity
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.repository.AlertRepository
import java.util.Calendar
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

    override suspend fun hasAlertedToday(
        packageName: String,
        severity: AlertSeverity
    ): Boolean {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        return alertHistoryDao.hasAlertedToday(
            packageName = packageName,
            severity = severity.name,
            startOfDay = startOfDay
        )
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