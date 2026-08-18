package sutanu.apps.zenith.presentation.home

import sutanu.apps.zenith.domain.model.BedtimeInfo
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.model.UsageOverview

data class HomeUiState(
    val isLoading: Boolean = true,
    val overview: UsageOverview? = null,
    val bedtime: BedtimeInfo? = null,
    val alerts: List<HomeAlert> = emptyList(),
    val errorMessage: String? = null,
)
