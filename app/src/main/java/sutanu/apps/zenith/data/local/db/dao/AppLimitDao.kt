package sutanu.apps.zenith.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

@Dao
interface AppLimitDao {
    @Query("SELECT * FROM app_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppLimit(packageName: String): AppLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppLimit(appLimit: AppLimitEntity)
}