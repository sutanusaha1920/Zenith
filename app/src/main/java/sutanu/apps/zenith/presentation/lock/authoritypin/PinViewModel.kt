package sutanu.apps.zenith.presentation.lock.authoritypin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.model.PinSetupStep
import sutanu.apps.zenith.domain.model.PinUiState
import sutanu.apps.zenith.domain.repository.AuthRepository
import sutanu.apps.zenith.domain.usecase.security.ValidatePinUseCase
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val validatePinUseCase: ValidatePinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState: StateFlow<PinUiState> = _uiState.asStateFlow()

    init {
        determineRoutingFlow()

    }

    private fun determineRoutingFlow() {
        viewModelScope.launch {
            val savedPin = repository.parentalPin.first()
            if (savedPin.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    currentStep = PinSetupStep.CREATE,
                    headerSubtitleText = "Create a 6-digit PIN"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    currentStep = PinSetupStep.VALIDATE,
                    headerSubtitleText = "Enter your PIN to continue"
                )
            }
        }
    }

    fun onKeyClick(digit: String) {
        val currentPin = _uiState.value.enteredPin
        if (currentPin.length < 6) {
            val newPin = currentPin + digit
            _uiState.value = _uiState.value.copy(
                enteredPin = newPin,
                isError = false,
                errorMessage = null
            )

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
        when (_uiState.value.currentStep) {
            PinSetupStep.CREATE -> {
                _uiState.value = _uiState.value.copy(
                    enteredPin = "",
                    firstTimePinDraft = pin,
                    currentStep = PinSetupStep.CONFIRM,
                    headerSubtitleText = "Confirm your PIN"
                )
            }

            PinSetupStep.CONFIRM -> {
                val draftedPin = _uiState.value.firstTimePinDraft
                if (pin == draftedPin) {
                    viewModelScope.launch {
                        repository.savePin(pin) // saves PIN
                        _uiState.value = _uiState.value.copy(isSuccess = true)
                    }
                } else {
                    // Restarts matrix state to step one with error feedback
                    _uiState.value = _uiState.value.copy(
                        isError = true,
                        errorMessage = "PINs did not match. Restart setup"
                    )
                    viewModelScope.launch {
                        delay(800)
                        _uiState.value = _uiState.value.copy(
                            enteredPin = "",
                            firstTimePinDraft = "",
                            currentStep = PinSetupStep.CREATE,
                            headerSubtitleText = "Create a 6-digit PIN",
                            isError = false
                        )
                    }
                }
            }

            PinSetupStep.VALIDATE -> {
                viewModelScope.launch {
                    val isValid = validatePinUseCase(pin)
                    if (isValid) {
                        _uiState.value = _uiState.value.copy(isSuccess = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isError = true,
                            errorMessage = "Incorrect PIN code. Try again"
                        )
                        delay(800)
                        _uiState.value = _uiState.value.copy(
                            enteredPin = "",
                            isError = false
                        )
                    }
                }
            }
        }
    }
}