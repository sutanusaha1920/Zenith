package sutanu.apps.zenith.presentation.bedtime.bedtime_ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.model.BedtimeUiState
import sutanu.apps.zenith.presentation.bedtime.BedtimeViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.OuterCardStrokePrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.WarningPrimary
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BedtimeScreen(
    viewModel: BedtimeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    BedtimeContent(
        state = state,
        onToggleBedtimeMode = { viewModel.toggleBedtimeMode(it) }
    )
}

@Composable
fun BedtimeContent(
    state: BedtimeUiState,
    onToggleBedtimeMode: (Boolean) -> Unit
) {
    val startAngle = calculateTimeAngle(state.startTime)
    val sweepAngle = calculateSweepAngle(state.startTime, state.endTime)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
    ) {

        // Header title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bedtime Mode",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )

                    Text(
                        text = "Block device during sleep hours",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Switch(
                    checked = state.isScheduleEnabled,
                    onCheckedChange = onToggleBedtimeMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = InfoPrimary,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = InputBg
                    )
                )
            }
        }

        // Bedtime progress arc
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val strokeWidth = 16.dp.toPx()
                        val arcRadius = (size.width - strokeWidth) / 2
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                        drawArc(
                            color = Color(0xFF1c2536),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = Color(0xFF2a4e7a),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        val angleRad = Math.toRadians(startAngle.toDouble())
                        val handleX = (size.width / 2) + arcRadius * cos(angleRad)
                        val handleY = (size.height / 2) + arcRadius * sin(angleRad)
                        drawCircle(
                            color = WarningPrimary,
                            radius = 8.dp.toPx(),
                            center = Offset(handleX.toFloat(), handleY.toFloat())
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bedtime_moon),
                            contentDescription = "Bedtime Moon",
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))


                        Text(
                            text = "Duration",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Start vs End horizontal row split
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Start Time
                            Image(
                                painter = painterResource(id = R.drawable.ic_bedtime_moon_off),
                                contentDescription = "Start logo",
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Start",
                                color = TextSecondary,
                                fontSize = 18.sp,
                                fontFamily = Poppins
                            )
                        }

                        Text(
                            text = state.startTime,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = TextSecondary.copy(alpha = 0.25f),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .width(1.dp)
                            .height(40.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sun),
                                contentDescription = "End logo",
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "End",
                                color = TextSecondary,
                                fontSize = 18.sp,
                                fontFamily = Poppins
                            )
                        }
                        Text(
                            text = state.endTime,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Time Schedule
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Schedule",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // input container for sleep
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bedtime start",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(InputBg)
                                .border(1.dp, OuterCardStrokePrimary, RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.startTime,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = "clock",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // input container for wake up
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wake up time",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(InputBg)
                                .border(1.dp, OuterCardStrokePrimary, RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.endTime,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = "clock",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Allowed During Bedtime",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ExceptionToggleItem(
                    label = "Phone calls",
                    icon = R.drawable.ic_phone,
                    checked = true,
                    onToggle = {}
                )

                ExceptionToggleItem(
                    label = "Alarm clock",
                    icon = R.drawable.ic_alarm,
                    checked = true,
                    onToggle = {}
                )

                ExceptionToggleItem(
                    label = "Wi-Fi / Internet",
                    icon = R.drawable.ic_network,
                    checked = true,
                    onToggle = {}
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AlertPrimary.copy(alpha = 0.08f))
                    .border(1.dp, AlertPrimary.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zenith_shield),
                        contentDescription = "info",
                        modifier = Modifier
                            .size(15.dp)
                            .padding(end = 12.dp, top = 2.dp)
                    )
                    Text(
                        text = "During bedtime, all apps and device usage are blocked. Only selected exceptions above remain accessible.",
                        color = AlertPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}

@Composable
fun ExceptionToggleItem(
    label: String,
    icon: Int,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "icon",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = InfoPrimary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = ControlDark
            )
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun BedtimeScreenPreview() {
    BedtimeContent(
        state = BedtimeUiState(
            isScheduleEnabled = true,
            startTime = "12:00 AM",
            endTime = "07:30 AM"
        ),
        onToggleBedtimeMode = {}
    )
}

private fun calculateTimeAngle(time: String): Float {
    return try {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
        val localTime = LocalTime.parse(time, formatter)
        val hour = localTime.hour % 12
        val minute = localTime.minute
        (hour * 30f) + (minute * 0.5f) - 90f
    } catch (e: Exception) {
        -150f // Default for 10:00
    }
}

private fun calculateSweepAngle(start: String, end: String): Float {
    return try {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
        val startTime = LocalTime.parse(start, formatter)
        val endTime = LocalTime.parse(end, formatter)

        val startMinutes = startTime.hour * 60 + startTime.minute
        var endMinutes = endTime.hour * 60 + endTime.minute

        if (endMinutes < startMinutes) {
            endMinutes += 24 * 60
        }

        val durationMinutes = endMinutes - startMinutes
        // On a 12-hour clock visual, we represent the proportion of the 12-hour cycle.
        // If sleep is 9 hours, it's (9/12) * 360 = 270 degrees.
        (durationMinutes / 720f) * 360f
    } catch (e: Exception) {
        270f // Default for 9 hours
    }
}
