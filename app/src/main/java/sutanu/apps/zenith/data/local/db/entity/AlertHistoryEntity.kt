package sutanu.apps.zenith.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert_history")
data class AlertHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val severity: String,
    val timeStamp: Long,
    val usageAtTrigger: String,
    val limitAtTrigger: String
)