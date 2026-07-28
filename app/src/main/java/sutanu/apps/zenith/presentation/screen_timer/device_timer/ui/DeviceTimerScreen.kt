package sutanu.apps.zenith.presentation.screen_timer.device_timer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.tooling.preview.Preview
import sutanu.apps.zenith.domain.model.DeviceTimerUiState
import sutanu.apps.zenith.presentation.ui.theme.OuterCardStrokePrimary
import sutanu.apps.zenith.presentation.ui.theme.SurfaceSecondary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun DeviceTimerScreen(viewModel: DeviceTimerViewModel) {
    val state by viewModel.uiState.collectAsState()
    DeviceTimerContent(
        state = state,
        onUpdateLimit = { viewModel.updateDeviceLimit(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceTimerContent(
    state: DeviceTimerUiState,
    onUpdateLimit: (Float) -> Unit
) {
    val limitMinutes = state.deviceLimitHours * 60
    val progressPercentage = if (limitMinutes > 0) {
        (state.totalTimeUsedMinutes / limitMinutes).coerceIn(0f, 1f)
    } else {
        0f
    }

    val sweepAngleAnimated by animateFloatAsState(
        targetValue = progressPercentage * 240f,
        animationSpec = tween(durationMillis = 800),
        label = "Screen Time Animation",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfacePrimary)
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceSecondary)
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
                    val strokeW = 12.dp.toPx()
                    val outerPadding = 10.dp.toPx()
                    val innerGap = 16.dp.toPx()

                    // Inner Arc
                    drawArc(
                        color = ControlDark,
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round),
                        size = androidx.compose.ui.geometry.Size(
                            width = size.width - outerPadding * 2 - innerGap * 2,
                            height = size.height - outerPadding * 2 - innerGap * 2
                        ),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = outerPadding + innerGap,
                            y = outerPadding + innerGap
                        )
                    )

                    // Outer Arc
                    drawArc(
                        color = if (state.totalTimeUsedMinutes > limitMinutes) AlertPrimary else InfoPrimary,
                        startAngle = 150f,
                        sweepAngle = sweepAngleAnimated,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round),
                        size = androidx.compose.ui.geometry.Size(
                            width = size.width - outerPadding * 2,
                            height = size.height - outerPadding * 2
                        ),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = outerPadding,
                            y = outerPadding
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val formattedLimit = if (state.deviceLimitHours % 1 == 0f) 
                        state.deviceLimitHours.toInt().toString() 
                    else 
                        state.deviceLimitHours.toString()

                    Text(
                        text = "${state.totalTimeUsedMinutes / 60}h ${state.totalTimeUsedMinutes % 60}m",
                        color = if (state.totalTimeUsedMinutes > limitMinutes) AlertPrimary else TextPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )

                    Text(
                        text = "of ${formattedLimit}h limit",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontFamily = Poppins
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceSecondary)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Limit",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )

                val formattedLimit = if (state.deviceLimitHours % 1 == 0f) 
                    state.deviceLimitHours.toInt().toString() 
                else 
                    state.deviceLimitHours.toString()

                Text(
                    text = "${formattedLimit}h",
                    color = InfoPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = state.deviceLimitHours,
                onValueChange = onUpdateLimit,
                valueRange = 1f..12f,
                colors = SliderDefaults.colors(
                    activeTrackColor = InfoPrimary,
                    inactiveTrackColor = ControlDark
                ),
                modifier = Modifier.padding(horizontal = 12.dp),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(InfoPrimary, CircleShape)
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier
                            .height(12.dp)
                            .clip(CircleShape)
                            .border(1.dp, OuterCardStrokePrimary, CircleShape),
                        colors = SliderDefaults.colors(
                            activeTrackColor = InfoPrimary,
                            inactiveTrackColor = ControlDark
                        ),
                        thumbTrackGapSize = 0.dp,
                        drawStopIndicator = { }
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween)
            {
                Text(
                    text = "1h",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )

                Text(
                    text = "12h",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeviceTimerPreview() {
    ZenithTheme {
        DeviceTimerContent(
            state = DeviceTimerUiState(
                totalTimeUsedMinutes = 135,
                deviceLimitHours = 4.0f
            ),
            onUpdateLimit = {}
        )
    }
}
