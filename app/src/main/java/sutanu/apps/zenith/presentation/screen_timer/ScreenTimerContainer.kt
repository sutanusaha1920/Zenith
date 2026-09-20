package sutanu.apps.zenith.presentation.screen_timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sutanu.apps.zenith.presentation.navigation.TimerTab
import sutanu.apps.zenith.presentation.screen_timer.app_timer.AppTimerViewModel
import sutanu.apps.zenith.presentation.screen_timer.device_timer.DeviceTimerViewModel
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary

import androidx.compose.ui.tooling.preview.Preview
import sutanu.apps.zenith.domain.model.AppInfo
import sutanu.apps.zenith.domain.model.AppTimer
import sutanu.apps.zenith.domain.model.DeviceTimer
import sutanu.apps.zenith.presentation.screen_timer.app_timer.ui.AppTimerContent
import sutanu.apps.zenith.presentation.screen_timer.device_timer.ui.DeviceTimerContent
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun ScreenTimerContainer(
    deviceTimerViewModel: DeviceTimerViewModel = hiltViewModel(),
    appTimerViewModel: AppTimerViewModel = hiltViewModel()
) {
    val deviceState by deviceTimerViewModel.uiState.collectAsState()
    val appState by appTimerViewModel.uiState.collectAsState()

    ScreenTimerContent(
        deviceState = deviceState,
        appState = appState,
        onToggleMasterTimer = { deviceTimerViewModel.toggleMasterTimer(it) },
        onUpdateDeviceLimit = { deviceTimerViewModel.updateDeviceLimit(it) },
        onAddLimitClick = { appTimerViewModel.setAddLimitVisible(true) },
        onDeleteLimit = { appTimerViewModel.deleteLimit(it) },
        onSelectApp = { appTimerViewModel.selectApp(it) },
        onUpdateDraftSlider = { appTimerViewModel.updateDraftSlider(it) },
        onApplyLimit = { appTimerViewModel.applyLimit() },
        onDismissDialog = { appTimerViewModel.setAddLimitVisible(false) }
    )
}

@Composable
fun ScreenTimerContent(
    deviceState: DeviceTimer,
    appState: AppTimer,
    onToggleMasterTimer: (Boolean) -> Unit,
    onUpdateDeviceLimit: (Float) -> Unit,
    onAddLimitClick: () -> Unit,
    onDeleteLimit: (String) -> Unit,
    onSelectApp: (AppInfo) -> Unit,
    onUpdateDraftSlider: (Float) -> Unit,
    onApplyLimit: () -> Unit,
    onDismissDialog: () -> Unit
) {
    var activeTab by remember { mutableStateOf(TimerTab.ENTIRE_DEVICE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Title control header panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Screen Timer",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )

                Text(
                    text = "Monitor and limit usage",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Switch(
                checked = deviceState.isTimerEnabled,
                onCheckedChange = onToggleMasterTimer,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = InfoPrimary,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = ControlDark
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BackgroundPrimary)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp, 0.dp, 0.dp, 10.dp))
                    .background(if (activeTab == TimerTab.ENTIRE_DEVICE) InfoPrimary else Color.Transparent)
                    .clickable { activeTab = TimerTab.ENTIRE_DEVICE },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Entire Device",
                    color = if (activeTab == TimerTab.ENTIRE_DEVICE) TextPrimary else TextSecondary,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp))
                    .background(if (activeTab == TimerTab.INDIVIDUAL_APPS) InfoPrimary else Color.Transparent)
                    .clickable { activeTab = TimerTab.INDIVIDUAL_APPS },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Individual Apps",
                    color = if (activeTab == TimerTab.INDIVIDUAL_APPS) TextPrimary else TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (activeTab) {
            TimerTab.ENTIRE_DEVICE -> DeviceTimerContent(
                state = deviceState,
                onUpdateLimit = onUpdateDeviceLimit
            )

            TimerTab.INDIVIDUAL_APPS -> AppTimerContent(
                state = appState,
                onAddLimitClick = onAddLimitClick,
                onDeleteLimit = onDeleteLimit,
                onSelectApp = onSelectApp,
                onUpdateDraftSlider = onUpdateDraftSlider,
                onApplyLimit = onApplyLimit,
                onDismissDialog = onDismissDialog
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
fun ScreenTimerContainerPreview() {
    ZenithTheme {
        ScreenTimerContent(
            deviceState = DeviceTimer(),
            appState = AppTimer(),
            onToggleMasterTimer = {},
            onUpdateDeviceLimit = {},
            onAddLimitClick = {},
            onDeleteLimit = {},
            onSelectApp = {},
            onUpdateDraftSlider = {},
            onApplyLimit = {},
            onDismissDialog = {}
        )
    }
}
