package sutanu.apps.zenith.presentation.screen_timer.device_timer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sutanu.apps.zenith.presentation.screen_timer.device_timer.DeviceTimerViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary

@Composable
fun DeviceTimerScreen(viewModel: DeviceTimerViewModel) {

    val state by viewModel.uiState.collectAsState()
    val limitMinutes = state.deviceLimitHours * 60
    val progressPercentage = (state.totalTimeUsedMinutes / limitMinutes).coerceIn(0f, 1f)

    val sweepAngleAnimated by animateFloatAsState(
        targetValue = progressPercentage * 240f,
        animationSpec = tween(durationMillis = 800),
        label = "Screen Time Animation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfacePrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeW = 14.dp.toPx()
                drawArc(
                    color = SurfacePrimary,
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )

                drawArc(
                    color = if (state.totalTimeUsedMinutes > limitMinutes) AlertPrimary else InfoPrimary,
                    startAngle = 150f,
                    sweepAngle = sweepAngleAnimated,
                    useCenter = false,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${state.totalTimeUsedMinutes / 60}h ${state.totalTimeUsedMinutes % 60}m",
                    color = if (state.totalTimeUsedMinutes > limitMinutes) AlertPrimary else InfoPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )

                Text(
                    text = "of ${state.deviceLimitHours}h limit",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Daily Limit",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )

            Text(
                text = "${state.deviceLimitHours}h",
                color = InfoPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = state.deviceLimitHours,
            onValueChange = { viewModel.updateDeviceLimit(it) },
            valueRange = 1f..12f,
            steps = 21,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = InfoPrimary,
                inactiveTrackColor = ControlDark
            )
        )
    }
}