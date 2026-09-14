package sutanu.apps.zenith.presentation.screen_timer.device_timer

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.services.AccessibilityMonitor
import sutanu.apps.zenith.domain.model.DeviceTimer
import sutanu.apps.zenith.domain.usecase.timer.GetUsageStatsUseCase
import sutanu.apps.zenith.domain.usecase.timer.ManageDeviceLimitUseCase
import javax.inject.Inject

@HiltViewModel
class DeviceTimerViewModel @Inject constructor(
    private val manageDeviceLimitUseCase: ManageDeviceLimitUseCase,
    private val getUsageStatsUseCase: GetUsageStatsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<DeviceTimer> = combine(
        manageDeviceLimitUseCase.getLimit(),
        manageDeviceLimitUseCase.isEnabled(),
        getUsageStatsUseCase.getTodayUsage(),
        _refreshTrigger
    ) {
        limit, enabled, usage, _ ->
        val needsUsage = !getUsageStatsUseCase.hasPermission()
        val needsAccess = !isAccessibilityServiceEnabled(context)
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
        initialValue = DeviceTimer()
    )

    fun refreshPermissions() {
        _refreshTrigger.value += 1
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponentName = ComponentName(context, AccessibilityMonitor::class.java)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        return false
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