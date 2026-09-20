package sutanu.apps.zenith.presentation.screen_timer.device_timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.model.DeviceTimer
import sutanu.apps.zenith.domain.usecase.monitor.GetAccessibilityStatusUseCase
import sutanu.apps.zenith.domain.usecase.timer.GetUsageStatsUseCase
import sutanu.apps.zenith.domain.usecase.timer.ManageDeviceLimitUseCase
import javax.inject.Inject

@HiltViewModel
class DeviceTimerViewModel @Inject constructor(
    private val manageDeviceLimitUseCase: ManageDeviceLimitUseCase,
    private val getUsageStatsUseCase: GetUsageStatsUseCase,
    private val getAccessibilityStatusUseCase: GetAccessibilityStatusUseCase
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<DeviceTimer> = combine(
        manageDeviceLimitUseCase.getLimit(),
        manageDeviceLimitUseCase.isEnabled(),
        getUsageStatsUseCase.getTodayUsage(),
        _refreshTrigger
    ) { limit, enabled, usage, _ ->
        val needsUsage = !getUsageStatsUseCase.hasPermission()
        val needsAccess = !getAccessibilityStatusUseCase()
        Log.d("ZenithPermissions", "Re-evaluating permissions - needsUsage: $needsUsage, needsAccess: $needsAccess")
        DeviceTimer(
            deviceLimitHours = limit,
            isTimerEnabled = enabled,
            totalTimeUsedMinutes = usage,
            needsUsagePermission = needsUsage,
            needsAccessibilityPermission = needsAccess
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DeviceTimer(
            needsUsagePermission = !getUsageStatsUseCase.hasPermission(),
            needsAccessibilityPermission = !getAccessibilityStatusUseCase()
        )
    )

    fun refreshPermissions() {
        _refreshTrigger.value += 1
    }

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