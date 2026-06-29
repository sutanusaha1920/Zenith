package sutanu.apps.zenith.presentation.bedtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.model.BedtimeUiState
import sutanu.apps.zenith.domain.usecase.bedtime.RemoveBedtimeExceptionUseCase
import sutanu.apps.zenith.domain.usecase.bedtime.UpdateBedtimeScheduleUseCase
import javax.inject.Inject

@HiltViewModel
class BedtimeViewModel @Inject constructor(
    private val updateBedtimeScheduleUseCase: UpdateBedtimeScheduleUseCase,
    private val removeBedtimeExceptionUseCase: RemoveBedtimeExceptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BedtimeUiState())
    val uiState: StateFlow<BedtimeUiState> = _uiState.asStateFlow()

    fun onToggleSchedule(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isScheduleEnabled = enabled)
        viewModelScope.launch {
            updateBedtimeScheduleUseCase.toggleSchedule(enabled)
        }
    }

    fun onTimeChanged(start: String, end: String) {
        _uiState.value = _uiState.value.copy(startTime = start, endTime = end)
        viewModelScope.launch {
            updateBedtimeScheduleUseCase.changeInterval(start, end)
        }
    }

    fun removeAppFromExceptions(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeBedtimeExceptionUseCase(packageName)
        }
    }
}