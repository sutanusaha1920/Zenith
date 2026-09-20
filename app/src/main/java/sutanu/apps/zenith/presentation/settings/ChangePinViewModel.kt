package sutanu.apps.zenith.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.usecase.security.UpdatePinUseCase
import sutanu.apps.zenith.presentation.settings.uistate.ChangePinUiState
import javax.inject.Inject

@HiltViewModel
class ChangePinViewModel @Inject constructor(
    private val updatePinUseCase: UpdatePinUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePinUiState())
    val uiState = _uiState.asStateFlow()

    fun onCurrentPinChange(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(currentPin = pin, errorMessage = null) }
        }
    }

    fun onNewPinChange(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(newPin = pin, errorMessage = null) }
        }
    }

    fun onConfirmPinChange(pin: String) {
        if (pin.length <= 6) {
            _uiState.update { it.copy(confirmPin = pin, errorMessage = null) }
        }
    }

    fun toggleCurrentPinVisibility() {
        _uiState.update { it.copy(isCurrentPinVisible = !it.isCurrentPinVisible) }
    }

    fun toggleNewPinVisibility() {
        _uiState.update { it.copy(isNewPinVisible = !it.isNewPinVisible) }
    }

    fun toggleConfirmPinVisibility() {
        _uiState.update { it.copy(isConfirmPinVisible = !it.isConfirmPinVisible) }
    }

    fun onUpdatePinClick() {
        viewModelScope.launch {
            val currentState = _uiState.value

            val result = updatePinUseCase(currentState.currentPin, currentState.newPin, currentState.confirmPin)

            when (result) {
                is UpdatePinUseCase.Result.Success ->
                    _uiState.update { it.copy(isSuccess = true, errorMessage = null) }

                is UpdatePinUseCase.Result.IncorrectCurrentPin ->
                    _uiState.update { it.copy(errorMessage = "Current PIN is incorrect.") }

                is UpdatePinUseCase.Result.PinsDoNotMatch ->
                    _uiState.update { it.copy(errorMessage = "New PIN and Confirmed PIN do not match.") }

                is UpdatePinUseCase.Result.InvalidFormat ->
                    _uiState.update { it.copy(errorMessage = "PIN must be 6 digits.") }
            }
        }
    }
}
