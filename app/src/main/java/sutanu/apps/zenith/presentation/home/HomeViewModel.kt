package sutanu.apps.zenith.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import sutanu.apps.zenith.domain.usecase.home.GetBedtimeInfoUseCase
import sutanu.apps.zenith.domain.usecase.home.GetHomeOverViewUseCase
import sutanu.apps.zenith.domain.usecase.home.GetRecentAlertsUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeOverViewUseCase: GetHomeOverViewUseCase,
    private val getRecentAlertsUseCase: GetRecentAlertsUseCase,
    private val getBedtimeInfoUseCase: GetBedtimeInfoUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        getHomeOverViewUseCase(),
        getRecentAlertsUseCase(),
        getBedtimeInfoUseCase()
    ) { overview, alerts, bedtime ->
        HomeUiState(
            isLoading = false,
            overview = overview,
            alerts = alerts,
            bedtime = bedtime,
            errorMessage = null
        )
    }
        .catch { e ->
            emit(HomeUiState(isLoading = false, errorMessage = e.localizedMessage))
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}