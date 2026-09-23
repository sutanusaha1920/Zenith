package sutanu.apps.zenith.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

@Dao
interface AppLimitDao {

    @Query("SELECT * FROM app_limits ORDER BY appName ASC")
    fun getAllAppLimitsFlow(): Flow<List<AppLimitEntity>>

    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppLimit(packageName: String): AppLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppLimit(appLimit: AppLimitEntity)

    @Query("UPDATE app_limits SET dailyMinutesUsed = :minutes WHERE packageName = :packageName")
    suspend fun updateDailyAppUsage(packageName: String, minutes: Int)

    @Query("UPDATE app_limits SET overrideExpirationTimestamp = :expirationTimestamp WHERE packageName = :packageName")
    suspend fun grantAppOverride(packageName: String, expirationTimestamp: Long)

    @Query("DELETE FROM app_limits WHERE packageName = :packageName")
    suspend fun deleteAppLimit(packageName: String)
}
