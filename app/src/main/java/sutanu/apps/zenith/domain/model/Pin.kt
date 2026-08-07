package sutanu.apps.zenith.domain.model

enum class PinSetupStep { CREATE, CONFIRM, VALIDATE }
data class PinUiState(
    val enteredPin: String = "",
    val firstTimePinDraft: String ="",
    val currentStep: PinSetupStep = PinSetupStep.VALIDATE,
    val headerSubtitleText: String = "Enter your PIN to continue",
    val isPinVisible: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
