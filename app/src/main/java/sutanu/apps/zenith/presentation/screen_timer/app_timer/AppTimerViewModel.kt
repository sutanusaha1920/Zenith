package sutanu.apps.zenith.presentation.screen_timer.app_timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.model.AppInfo
import sutanu.apps.zenith.domain.model.AppTimer
import sutanu.apps.zenith.domain.usecase.apps.GetInstalledAppsUseCase
import sutanu.apps.zenith.domain.usecase.apps.ManageAppLimitsUseCase
import javax.inject.Inject

@HiltViewModel
class AppTimerViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val manageAppLimitsUseCase: ManageAppLimitsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppTimer())
    val uiState: StateFlow<AppTimer> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            manageAppLimitsUseCase.executeGetLimits().collect { limits ->
                _uiState.update { it.copy(individualLimits = limits) }
            }
        }

        viewModelScope.launch {
            val apps = getInstalledAppsUseCase()
            _uiState.value = _uiState.value.copy(installedAppsList = apps)
        }
    }

    fun setAddLimitVisible(visible: Boolean) {
        _uiState.update { current ->
            if (!visible) {
                current.copy(
                    showAddLimitSection = false,
                    selectedAppToLimit = null,
                    draftLimitHours = 1.0f
                )
            } else {
                current.copy(showAddLimitSection = true)
            }
        }
    }

    fun selectApp(app: AppInfo?) {
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
            Log.d("ZenithViewModel", "Deleting limit for: $packageName")
            manageAppLimitsUseCase.executeDeleteLimit(packageName)
        }
    }
}