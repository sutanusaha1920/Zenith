package sutanu.apps.zenith.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity

@Dao
interface SosContactsDao {
    @Query("SELECT * FROM sos_contacts")
    fun getAllContactsFlow(): Flow<List<SosContactEntity>>

    @Query("SELECT phoneNumber FROM sos_contacts")
    suspend fun getAllContactNumbers(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: SosContactEntity)

    @Delete
    suspend fun deleteContact(contact: SosContactEntity)

}