package sutanu.apps.zenith.domain.usecase.security

import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class ToggleUninstallProtectionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val validatePinUseCase: ValidatePinUseCase
) {
    suspend operator fun invoke(enable: Boolean, pin: String? = null): Boolean {
        if (!enable) {
            if (pin == null || !validatePinUseCase(pin)) {
                return false
            }
        }

        authRepository.setUninstallProtection(enable)
        return true
    }
}