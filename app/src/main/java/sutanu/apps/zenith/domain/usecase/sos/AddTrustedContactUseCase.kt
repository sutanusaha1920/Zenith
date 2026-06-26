package sutanu.apps.zenith.domain.usecase.sos

import sutanu.apps.zenith.domain.repository.SosRepository
import javax.inject.Inject

class AddTrustedContactUseCase @Inject constructor(
    private val repository: SosRepository
) {

    suspend operator fun invoke(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) return
        repository.addContact(name, phone)
    }
}