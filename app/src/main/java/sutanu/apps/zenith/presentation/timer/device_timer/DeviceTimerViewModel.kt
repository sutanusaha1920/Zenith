package sutanu.apps.zenith.presentation.timer.device_timer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import sutanu.apps.zenith.domain.model.DeviceTimerUiState
import sutanu.apps.zenith.domain.usecase.timer.ManageDeviceLimitUseCase
import javax.inject.Inject

@HiltViewModel
class DeviceTimerViewModel @Inject constructor(
    private val manageDeviceLimitUseCase: ManageDeviceLimitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceTimerUiState())
    val uiState: StateFlow<DeviceTimerUiState> = _uiState.asStateFlow()

    fun toggleMasterTimer(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isTimerEnabled = enabled)
    }

    fun updateDeviceLimit(hours: Float) {
        _uiState.value = _uiState.value.copy(deviceLimitHours = hours)
    }
}