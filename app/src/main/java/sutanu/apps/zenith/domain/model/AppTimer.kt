package sutanu.apps.zenith.domain.model

import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

data class AppTimer(
    val individualLimits: List<AppLimitEntity> = emptyList(),
    val installedAppsList: List<AppInfo> = emptyList(),
    val showAddLimitSection: Boolean = false,
    val selectedAppToLimit: AppInfo? = null,
    val draftLimitHours: Float = 1.0f
)
