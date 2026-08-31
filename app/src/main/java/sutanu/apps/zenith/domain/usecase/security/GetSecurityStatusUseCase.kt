package sutanu.apps.zenith.domain.usecase.security

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sutanu.apps.zenith.domain.model.SecurityStatus
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class GetSecurityStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<SecurityStatus> {
        return combine(
            authRepository.isDeviceAdminActive,
            authRepository.isUninstallProtectionEnabled
        ) { isAdmin, isProtected ->
            SecurityStatus(isAdmin, isProtected)
        }
    }
}