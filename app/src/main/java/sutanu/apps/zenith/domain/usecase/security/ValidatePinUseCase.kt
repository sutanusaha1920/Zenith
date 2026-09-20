package sutanu.apps.zenith.domain.usecase.security

import kotlinx.coroutines.flow.first
import sutanu.apps.zenith.core.util.SecurityUtils
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class ValidatePinUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(inputPin: String): Boolean {
        if (inputPin.length != 6) return false
        val savedPinHash = repository.parentalPin.first()

        if (savedPinHash.isEmpty()) return false

        val inputHash = SecurityUtils.hashPin(inputPin)
        if (inputHash == savedPinHash) {
            return true
        }

        // Migration support for legacy unhashed 6-digit PINs
        if (savedPinHash.length == 6 && inputPin == savedPinHash) {
            repository.savePin(inputPin) // Re-saves as hashed
            return true
        }

        return false
    }
}