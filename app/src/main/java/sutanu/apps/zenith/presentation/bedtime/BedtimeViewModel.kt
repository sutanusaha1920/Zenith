package sutanu.apps.zenith.presentation.bedtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sutanu.apps.zenith.domain.model.BedtimeUiState
import sutanu.apps.zenith.domain.usecase.bedtime.GetBedtimeConfigurationUseCase
import sutanu.apps.zenith.domain.usecase.bedtime.UpdateBedtimeScheduleUseCase
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BedtimeViewModel @Inject constructor(
    private val updateBedtimeScheduleUseCase: UpdateBedtimeScheduleUseCase,
    private val getBedtimeConfigurationUseCase: GetBedtimeConfigurationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BedtimeUiState())
    val uiState: StateFlow<BedtimeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBedtimeConfigurationUseCase().collect { config ->
                _uiState.value = _uiState.value.copy(
                    isScheduleEnabled = config.isEnabled,
                    startTime = formatMilitaryToUserTime(config.startTime),
                    endTime = formatMilitaryToUserTime(config.endTime)
                )
            }
        }
    }

    fun toggleBedtimeMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isScheduleEnabled = enabled)
        viewModelScope.launch {
            updateBedtimeScheduleUseCase.toggleSchedule(enabled)
        }
    }

    fun updateScheduleWindow(start: String, end: String) {
        _uiState.value = _uiState.value.copy(startTime = start, endTime = end)
        viewModelScope.launch {
            updateBedtimeScheduleUseCase.changeInterval(start, end)
        }
    }

    private val userTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    private fun formatMilitaryToUserTime(time: String): String {
        return try {
            LocalTime.parse(time).format(userTimeFormatter)
        } catch (e: Exception) {
            time
        }
    }
}