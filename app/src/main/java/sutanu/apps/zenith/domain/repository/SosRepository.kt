package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity

interface SosRepository {
    fun getAllContactsFlow(): Flow<List<SosContactEntity>>

    suspend fun addContact(name: String, phone: String)

    suspend fun deleteContact(contact: SosContactEntity)
}