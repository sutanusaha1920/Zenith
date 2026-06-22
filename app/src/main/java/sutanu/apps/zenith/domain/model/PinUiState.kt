package sutanu.apps.zenith.domain.model

data class PinUiState(
    val enteredPin: String = "",
    val isPinVisible: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
