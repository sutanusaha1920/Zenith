package sutanu.apps.zenith.domain.usecase.sos

import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.domain.repository.SosRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val repository: SosRepository
) {
    suspend operator fun invoke(contact: SosContactEntity) =
        repository.deleteContact(contact)
}