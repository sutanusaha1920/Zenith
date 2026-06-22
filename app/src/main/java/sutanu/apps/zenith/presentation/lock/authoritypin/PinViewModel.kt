package sutanu.apps.zenith.presentation.lock.authoritypin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.model.PinUiState
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState: StateFlow<PinUiState> = _uiState.asStateFlow()

    fun onKeyClick(digit: String) {
        val currentPin = _uiState.value.enteredPin
        if (currentPin.length < 6) {
            val newPin = currentPin + digit
            _uiState.value = _uiState.value.copy(enteredPin = newPin, isError = false)

            if (newPin.length == 6) {
                verifyPin(newPin)
            }
        }
    }

    fun onDeleteClick() {
        val currentPin = _uiState.value.enteredPin
        if (currentPin.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                enteredPin = currentPin.dropLast(1),
                isError = false
            )
        }
    }

    fun togglePinVisibility() {
        _uiState.value = _uiState.value.copy(
            isPinVisible = !_uiState.value.isPinVisible
        )
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            val savedPinHash = authPreferences.parentalPin.first()

            // Setup fallback default PIN if none is created yet
            val targetPin = savedPinHash.ifEmpty { "123456" }

            if (savedPinHash == pin) {
                _uiState.value = _uiState.value.copy(isSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    enteredPin = "",
                    isError = true,
                    errorMessage = "Incorrect PIN"
                )
            }
        }
    }
}