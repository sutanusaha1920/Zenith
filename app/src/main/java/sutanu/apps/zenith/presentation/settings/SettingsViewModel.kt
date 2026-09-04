package sutanu.apps.zenith.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import sutanu.apps.zenith.domain.usecase.security.GetSecurityStatusUseCase
import sutanu.apps.zenith.presentation.settings.uistate.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSecurityStatusUseCase: GetSecurityStatusUseCase
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = getSecurityStatusUseCase()
        .map{ securityStatus ->
        SettingsUiState(
            securityStatus = securityStatus,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )
}