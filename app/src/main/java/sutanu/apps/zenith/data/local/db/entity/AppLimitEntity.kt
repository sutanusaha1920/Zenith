package sutanu.apps.zenith.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_limits")
data class AppLimitEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int,
    val dailyMinutesUsed: Int = 0,
    val isBlockedText: Boolean = false,
    val overrideExpirationTimestamp: Long = 0L
)
