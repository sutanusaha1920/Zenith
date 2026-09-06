package sutanu.apps.zenith.presentation.settings.uistate

data class DeletionProtectionUiState(
    val isDeletionProtectionEnabled: Boolean = true,
    val showNumpad: Boolean = false,
    val enteredPin: String = "",
    val isPinVisible: Boolean = false,
    val isPinError: Boolean = false,
    val errorMessage: String? = null
)
