package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.db.dao.SosContactsDao
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.domain.repository.SosRepository
import javax.inject.Inject

class SosRepositoryImpl @Inject constructor(
    private val sosContactsDao: SosContactsDao
) : SosRepository {
    override fun getAllContactsFlow(): Flow<List<SosContactEntity>> =
        sosContactsDao.getAllContactsFlow()

    override suspend fun addContact(name: String, phone: String) {
        val entity = SosContactEntity(contactName = name.trim(), phoneNumber = phone.trim())
        sosContactsDao.insertContact(entity)
    }

    override suspend fun deleteContact(contact: SosContactEntity) =
        sosContactsDao.deleteContact(contact)

}