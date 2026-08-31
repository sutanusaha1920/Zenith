package sutanu.apps.zenith.domain.usecase.security

import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class UpdatePinUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val validatePinUseCase: ValidatePinUseCase
) {
    sealed class Result {
        object Success : Result()
        object IncorrectCurrentPin : Result()
        object PinsDoNotMatch : Result()
        object InvalidFormat : Result()
    }

    suspend operator fun invoke(
        currentPin: String,
        newPin: String,
        confirmPin: String
    ): Result {
        if (!validatePinUseCase(currentPin)) return Result.IncorrectCurrentPin

        if (newPin.length != 6) return Result.InvalidFormat

        if (newPin != confirmPin) return Result.PinsDoNotMatch

        authRepository.savePin(newPin)
        return Result.Success
    }
}