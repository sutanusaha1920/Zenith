package sutanu.apps.zenith.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.repository.AuthRepository
import sutanu.apps.zenith.domain.usecase.security.GetSecurityStatusUseCase
import sutanu.apps.zenith.presentation.settings.uistate.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSecurityStatusUseCase: GetSecurityStatusUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        getSecurityStatusUseCase(),
        authRepository.themeMode
    ) { securityStatus, themeMode ->
        SettingsUiState(
            securityStatus = securityStatus,
            themeMode = themeMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            authRepository.setThemeMode(mode)
        }
    }
}