package sutanu.apps.zenith.presentation.screen_timer.device_timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.model.DeviceTimerUiState
import sutanu.apps.zenith.domain.usecase.timer.GetUsageStatsUseCase
import sutanu.apps.zenith.domain.usecase.timer.ManageDeviceLimitUseCase
import javax.inject.Inject

@HiltViewModel
class DeviceTimerViewModel @Inject constructor(
    private val manageDeviceLimitUseCase: ManageDeviceLimitUseCase,
    private val getUsageStatsUseCase: GetUsageStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<DeviceTimerUiState> = combine(
        manageDeviceLimitUseCase.getLimit(),
        manageDeviceLimitUseCase.isEnabled(),
        getUsageStatsUseCase.getTodayUsage()
    ) {
        limit, enabled, usage ->
        DeviceTimerUiState(
            deviceLimitHours = limit,
            isTimerEnabled = enabled,
            totalTimeUsedMinutes = usage,
            needsUsagePermission = !getUsageStatsUseCase.hasPermission()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeviceTimerUiState()
    )

    fun toggleMasterTimer(enabled: Boolean) {
        viewModelScope.launch {
            manageDeviceLimitUseCase.toggleTimer(enabled)
        }
    }

    fun updateDeviceLimit(hours: Float) {
        viewModelScope.launch {
            manageDeviceLimitUseCase.updateLimit(hours)
        }
    }
}