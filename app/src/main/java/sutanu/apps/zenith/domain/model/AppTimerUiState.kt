package sutanu.apps.zenith.domain.model

import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

data class AppTimerUiState(
    val individualLimits: List<AppLimitEntity> = emptyList(),
    val installedAppsList: List<AppLimitEntity> = emptyList(),
    val showAddLimitSection: Boolean = false,
    val selectedAppToLimit: AppInfoUiState? = null,
    val draftLimitHours: Float = 1.0f
)
