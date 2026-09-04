package sutanu.apps.zenith.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.usecase.security.GetSecurityStatusUseCase
import sutanu.apps.zenith.domain.usecase.security.ToggleUninstallProtectionUseCase
import sutanu.apps.zenith.presentation.settings.uistate.DeletionProtectionUiState
import javax.inject.Inject

private data class NumpadState(
    val showNumpad: Boolean = false,
    val enteredPin: String = "",
    val isPinError: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DeletionProtectionViewModel @Inject constructor(
    private val getSecurityStatusUseCase: GetSecurityStatusUseCase,
    private val toggleUninstallProtectionUseCase: ToggleUninstallProtectionUseCase
) : ViewModel() {
    private val _numpadState = MutableStateFlow(NumpadState())

    val uiState: StateFlow<DeletionProtectionUiState> = combine(
        getSecurityStatusUseCase(),
        _numpadState
    ) { securityStatus, numpad ->
        DeletionProtectionUiState(
            isDeletionProtectionEnabled = securityStatus.isUninstallProtected,
            showNumpad = numpad.showNumpad,
            enteredPin = numpad.enteredPin,
            isPinError = numpad.isPinError,
            errorMessage = numpad.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeletionProtectionUiState()
    )

    fun onToggleClicked(enable: Boolean) {
        if (enable) {
            viewModelScope.launch { toggleUninstallProtectionUseCase(true) }
            _numpadState.value = NumpadState()
        } else {
            _numpadState.value = NumpadState(showNumpad = true)
        }
    }

    fun onKeyClicked(digit: String) {
        val currentPin = _numpadState.value.enteredPin

        if (currentPin.length < 6) {
            val newPin = currentPin + digit
            _numpadState.value = _numpadState.value.copy(
                enteredPin = newPin,
                isPinError = false,
                errorMessage = null
            )

            if (newPin.length == 6) {
                verifyPinAndDisable(newPin)
            }
        }
    }

    fun onDeleteClicked() {
        val currentPin = _numpadState.value.enteredPin

        if (currentPin.isNotEmpty()) _numpadState.value = _numpadState.value.copy(
            enteredPin = currentPin.dropLast(1),
            isPinError = false,
            errorMessage = null
        )
    }

    fun onCancelClicked() {
        _numpadState.value = NumpadState(showNumpad = false)
    }

    private fun verifyPinAndDisable(pin: String) {
        viewModelScope.launch {
            val success = toggleUninstallProtectionUseCase(enable = false, pin = pin)

            if (success) {
                _numpadState.value = NumpadState(showNumpad = false)
            } else {
                _numpadState.value = _numpadState.value.copy(
                    enteredPin = "",
                    isPinError = true,
                    errorMessage = "Incorrect PIN!. Try again"
                )
            }
        }
    }
}