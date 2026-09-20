package sutanu.apps.zenith.presentation.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import sutanu.apps.zenith.domain.usecase.monitor.GetAccessibilityStatusUseCase
import sutanu.apps.zenith.domain.usecase.monitor.GetRecentAppLaunchesUseCase
import sutanu.apps.zenith.domain.usecase.monitor.GetWeeklyUsageUseCase
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val getAccessibilityStatusUseCase: GetAccessibilityStatusUseCase,
    private val getWeeklyUsageUseCase: GetWeeklyUsageUseCase,
    private val getRecentAppLaunchesUseCase: GetRecentAppLaunchesUseCase
) : ViewModel() {
    private val _refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MonitorUiState> = combine(
        getRecentAppLaunchesUseCase(),
        _refreshTrigger
    ) { recentLaunches, _ ->
        coroutineScope {
            val isAccessibilityActiveDeferred = async { getAccessibilityStatusUseCase() }
            val weeklyUsageDeferred = async { getWeeklyUsageUseCase() }

            MonitorUiState(
                isAccessibilityActive = isAccessibilityActiveDeferred.await(),
                weeklyUsage = weeklyUsageDeferred.await(),
                recentAppLaunches = recentLaunches,
                isLoading = false
            )
        }
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MonitorUiState()
        )

    fun refreshData() {
        _refreshTrigger.value += 1
    }
}
