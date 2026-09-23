package sutanu.apps.zenith.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AlertHistoryEntity

@Dao
interface AlertHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(entity: AlertHistoryEntity)

    @Query("SELECT * FROM alert_history ORDER BY timeStamp DESC")
    fun getRecentAlerts(): Flow<List<AlertHistoryEntity>>

    @Query("DELETE FROM alert_history WHERE id NOT IN (SELECT id FROM alert_history ORDER BY timeStamp DESC LIMIT 20)")
    suspend fun deleteOldAlert()

    @Query("DELETE FROM alert_history WHERE packageName = :packageName")
    suspend fun deleteAlertsForPackage(packageName: String)

    @Query("SELECT COUNT(*) > 0 FROM alert_history WHERE packageName = :packageName AND severity = :severity AND timeStamp >= :startOfDay")
    suspend fun hasAlertedToday(packageName: String, severity: String, startOfDay: Long): Boolean
}
