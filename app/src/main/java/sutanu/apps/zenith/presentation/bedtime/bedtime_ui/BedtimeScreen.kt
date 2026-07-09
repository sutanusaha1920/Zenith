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
                    .fillMaxWidth(),
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
                        color = TextPrimary,
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

        //
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

                        drawArc(
                            color = Color(0xFF1E293B),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = Color(0xFF2563EB),
                            startAngle = -140f,
                            sweepAngle = 290f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Square)
                        )

                        val angleRad = Math.toRadians(-140.0)
                        val radius = (size.width - strokeWidth) / 2
                        val handleX = (size.width / 2) + radius * cos(angleRad)
                        val handleY = (size.height / 2) + radius * sin(angleRad)
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
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Start",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = Poppins
                            )
                        }

                        Text(
                            text = state.startTime,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    VerticalDivider(
                        thickness = 1.dp,
                        color = TextSecondary,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .width(32.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sun),
                                contentDescription = "End logo",
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "End",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = Poppins
                            )
                        }
                        Text(
                            text = state.endTime,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
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
                    fontWeight = FontWeight.Bold,
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
                                fontWeight = FontWeight.Bold,
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
                                fontWeight = FontWeight.Bold,
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
                    fontWeight = FontWeight.Bold,
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
            startTime = "10:00 PM",
            endTime = "07:00 AM"
        ),
        onToggleBedtimeMode = {}
    )
}
