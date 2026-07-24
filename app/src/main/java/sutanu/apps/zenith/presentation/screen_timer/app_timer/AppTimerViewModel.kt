package sutanu.apps.zenith.presentation.screen_timer.app_timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.model.AppInfoUiState
import sutanu.apps.zenith.domain.model.AppTimerUiState
import sutanu.apps.zenith.domain.usecase.apps.GetInstalledAppsUseCase
import sutanu.apps.zenith.domain.usecase.apps.ManageAppLimitsUseCase
import javax.inject.Inject

@HiltViewModel
class AppTimerViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val manageAppLimitsUseCase: ManageAppLimitsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppTimerUiState())
    val uiState: StateFlow<AppTimerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            manageAppLimitsUseCase.executeGetLimits().collect { limits ->
                _uiState.value = _uiState.value.copy(individualLimits = limits)
            }
        }

        viewModelScope.launch {
            val apps = getInstalledAppsUseCase()
            _uiState.value = _uiState.value.copy(installedAppsList = apps)
        }
    }

    fun setAddLimitVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showAddLimitSection = visible)
    }

    fun selectApp(app: AppInfoUiState?) {
        _uiState.value = _uiState.value.copy(selectedAppToLimit = app)
    }

    fun updateDraftSlider(hours: Float) {
        _uiState.value = _uiState.value.copy(draftLimitHours = hours)
    }

    fun applyLimit() {
        val state = _uiState.value
        val app = state.selectedAppToLimit ?: return
        viewModelScope.launch {
            val entity = AppLimitEntity(
                packageName = app.packageName,
                appName = app.appName,
                dailyLimitMinutes = (state.draftLimitHours * 60).toInt()
            )
            manageAppLimitsUseCase.executeSaveLimit(entity)
            _uiState.value = _uiState.value.copy(
                showAddLimitSection = false,
                selectedAppToLimit = null
            )
        }
    }

    fun deleteLimit(packageName: String) {
        viewModelScope.launch {
            manageAppLimitsUseCase.executeDeleteLimit(packageName)
        }
    }
}