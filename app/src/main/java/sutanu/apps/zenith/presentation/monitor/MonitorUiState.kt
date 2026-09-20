package sutanu.apps.zenith.presentation.monitor

import sutanu.apps.zenith.domain.model.AppLaunchInfo
import sutanu.apps.zenith.domain.model.DailyUsage

data class MonitorUiState(
    val isAccessibilityActive: Boolean = false,
    val weeklyUsage: List<DailyUsage> = emptyList(),
    val recentAppLaunches: List<AppLaunchInfo> = emptyList(),
    val isLoading: Boolean = true,
)
