package sutanu.apps.zenith.presentation.settings.uistate

import sutanu.apps.zenith.domain.model.SecurityStatus

data class SettingsUiState(
    val securityStatus: SecurityStatus = SecurityStatus(),
    val themeMode: String = "dark",
)