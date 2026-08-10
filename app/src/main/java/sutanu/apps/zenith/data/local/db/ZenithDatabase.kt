package sutanu.apps.zenith.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sutanu.apps.zenith.data.local.db.dao.AlertHistoryDao
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.dao.SosContactsDao
import sutanu.apps.zenith.data.local.db.entity.AlertHistoryEntity
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity

@Database(
    entities = [SosContactEntity::class, AppLimitEntity::class, AlertHistoryEntity::class],
    version = 3,
    exportSchema = false
)

abstract class ZenithDatabase : RoomDatabase() {

    abstract fun sosContactsDao(): SosContactsDao
    abstract fun appLimitDao(): AppLimitDao

    abstract fun alertHistoryDao(): AlertHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: ZenithDatabase? = null

        fun getDatabase(context: Context): ZenithDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZenithDatabase::class.java,
                    "zenith_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}