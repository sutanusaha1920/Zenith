package sutanu.apps.zenith.presentation.settings.uistate

data class ChangePinUiState(
    val currentPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val isCurrentPinVisible: Boolean = false,
    val isNewPinVisible: Boolean = false,
    val isConfirmPinVisible: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)