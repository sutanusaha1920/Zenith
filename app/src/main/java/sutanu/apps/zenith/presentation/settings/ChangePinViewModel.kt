package sutanu.apps.zenith.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            _uiState.value = _uiState.value.copy(currentPin = pin, errorMessage = null)
        }
    }

    fun onNewPinChange(pin: String) {
        if (pin.length <= 6) {
            _uiState.value = _uiState.value.copy(newPin = pin, errorMessage = null)
        }
    }

    fun onConfirmPinChange(pin: String) {
        if (pin.length <= 6) {
            _uiState.value = _uiState.value.copy(confirmPin = pin, errorMessage = null)
        }
    }

    fun toggleCurrentPinVisibility() {
        _uiState.value = _uiState.value.copy(isCurrentPinVisible = !_uiState.value.isCurrentPinVisible)
    }

    fun toggleNewPinVisibility() {
        _uiState.value = _uiState.value.copy(isNewPinVisible = !_uiState.value.isNewPinVisible)
    }

    fun toggleConfirmPinVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPinVisible = !_uiState.value.isConfirmPinVisible)
    }

    fun onUpdatePinClick() {
        viewModelScope.launch {
            val currentState = _uiState.value

            val result = updatePinUseCase(currentState.currentPin, currentState.newPin, currentState.confirmPin)

            when (result) {
                is UpdatePinUseCase.Result.Success ->
                    _uiState.value = _uiState.value.copy(isSuccess = true, errorMessage = null)

                is UpdatePinUseCase.Result.IncorrectCurrentPin ->
                    _uiState.value = _uiState.value.copy(errorMessage = "Current PIN is incorrect.")

                is UpdatePinUseCase.Result.PinsDoNotMatch ->
                    _uiState.value = _uiState.value.copy(errorMessage = "New PIN and Confirmed PIN do not match.")

                is UpdatePinUseCase.Result.InvalidFormat ->
                    _uiState.value = _uiState.value.copy(errorMessage = "PIN must be 6 digits.")
            }
        }
    }
}