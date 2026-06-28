package sutanu.apps.zenith.domain.usecase.security

import kotlinx.coroutines.flow.first
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class ValidatePinUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(inputPin: String): Boolean {
        if (inputPin.length != 6) return false
        val savedPinHash = repository.parentalPin.first()

        if (savedPinHash.isEmpty()) return false
        return inputPin == savedPinHash
    }
}