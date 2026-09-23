package sutanu.apps.zenith.domain.model

import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity

data class AppTimer(
    val individualLimits: List<AppLimitEntity> = emptyList(),
    val installedAppsList: List<AppInfo> = emptyList(),
    val selectedAppToLimit: AppInfo? = null,
    val draftLimitHours: Float = 1.0f,
    val showAddLimitSection: Boolean = false,
    val overrideExtensionMinutes: Int = 15
)
