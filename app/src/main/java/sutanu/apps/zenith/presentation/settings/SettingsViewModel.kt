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
import sutanu.apps.zenith.presentation.settings.uistate.DialogUiState
import sutanu.apps.zenith.presentation.settings.uistate.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSecurityStatusUseCase: GetSecurityStatusUseCase,
    private val toggleUninstallProtectionUseCase: ToggleUninstallProtectionUseCase
) : ViewModel() {
    private val _dialogState = MutableStateFlow(DialogUiState())

    val uiState: StateFlow<SettingsUiState> = combine(
        getSecurityStatusUseCase(),
        _dialogState
    ) { securityStatus, dialogState ->
        SettingsUiState(
            securityStatus = securityStatus,
            showPinDialog = dialogState.showPinDialog,
            pinError = dialogState.pinError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onToggleUninstallProtection(enable: Boolean) {
        if (enable){
            viewModelScope.launch { toggleUninstallProtectionUseCase(true) }
        } else {
            _dialogState.value = DialogUiState(showPinDialog = true)
        }
    }

    fun onPinSubmitted(pin: String) {
        viewModelScope.launch {
            val success = toggleUninstallProtectionUseCase(enable = false, pin = pin)

            if (success){
                _dialogState.value = DialogUiState(showPinDialog = false, pinError = null)
            } else{
                _dialogState.value =
                    DialogUiState(showPinDialog = true, pinError = "Incorrect PIN!. Try again")
            }
        }
    }

    fun onDismissPinDialog() {
        _dialogState.value = DialogUiState(showPinDialog = false, pinError = null)
    }
}