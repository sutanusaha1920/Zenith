package sutanu.apps.zenith.presentation.home.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.model.AlertSeverity
import sutanu.apps.zenith.domain.model.BedtimeInfo
import sutanu.apps.zenith.domain.model.HomeAlert
import sutanu.apps.zenith.domain.model.UsageOverview
import sutanu.apps.zenith.presentation.home.HomeUiState
import sutanu.apps.zenith.presentation.home.HomeViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.OuterCardStrokePrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.SurfaceSecondary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.WarningPrimary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.listOf

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state
    )
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
) {
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A1128),
            BackgroundPrimary
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = InfoPrimary
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Overview",
                                color = TextPrimary,
                                fontFamily = Poppins,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            Text(
                                text = currentDate,
                                color = TextSecondary,
                                fontFamily = Poppins,
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SurfaceSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_zenith_shield),
                                contentDescription = "Zenith Logo",
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                // Info Cards
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1st Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                label = "Screen time today",
                                value = state.overview?.totalUsage ?: "0h 0m",
                                subInfo = "Limit: ${state.overview?.currentLimit ?: "None"}",
                                modifier = Modifier.weight(1f),
                                isAlert = true
                            )

                            StatCard(
                                label = if (state.bedtime?.bedtimeStatus == true) "Bedtime active" else "Bedtime inactive",
                                value = if (state.bedtime?.bedtimeStatus == true) state.bedtime.startTime else "Inactive",
                                subInfo = if (state.bedtime?.bedtimeStatus == true) "Ends ${state.bedtime.endTime}" else "Not set",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        //2nd Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                label = "Apps monitored",
                                value = "${state.overview?.monitoredAppsCount ?: 0} apps",
                                subInfo = "${state.overview?.exceededLimitsCount ?: 0} limits exceeded",
                                modifier = Modifier.weight(1f),
                                isAlert = (state.overview?.exceededLimitsCount ?: 0) > 0
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Recent Alerts
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceSecondary)
                            .border(
                                1.dp,
                                OuterCardStrokePrimary,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp),
                    ) {
                        // Sub Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_alert_triangle),
                                    contentDescription = "Alert Icon",
                                    tint = WarningPrimary,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.size(8.dp))

                                Text(
                                    text = "Recent Alerts",
                                    color = TextPrimary,
                                    fontFamily = Poppins,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (state.alerts.isNotEmpty()) "${state.alerts.size} new" else "",
                                color = TextSecondary,
                                fontFamily = Poppins,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.alerts.isEmpty()) {
                            Text(
                                text = "No new alerts yet",
                                color = TextSecondary,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )
                        } else {
                            state.alerts.forEachIndexed { index, alert ->
                                AlertItem(alert)

                                if (index < state.alerts.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp),
                                        thickness = 0.20.dp,
                                        color = TextSecondary.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    subInfo: String,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceSecondary)
            .border(
                if (isAlert)0.5.dp else 1.dp,
                if (isAlert) AlertPrimary.copy(alpha = 0.5f) else OuterCardStrokePrimary,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = Poppins,
            fontSize = 14.sp
        )

        Text(
            text = value,
            color = if (isAlert) AlertPrimary else TextPrimary,
            fontSize = 18.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subInfo,
            color = TextSecondary,
            fontFamily = Poppins,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun AlertItem(alert: HomeAlert) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    val appIcon = remember(alert.packageName) {
        try {
            alert.packageName?.let { packageManager.getApplicationIcon(it) }
        } catch (e: Exception) {
            null
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // App Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceSecondary),
            contentAlignment = Alignment.Center
        ){
            AsyncImage(
                model = appIcon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.ic_android_logo),
                error = painterResource(R.drawable.ic_android_logo)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Text
        Column(
            modifier = Modifier.weight(1f)
        ) {
            val alertMessage = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(alert.appName)
                }

                append(" ")
                append(when (alert.alertType) {
                    AlertSeverity.EXCEEDED -> "daily limit exceeded"
                    AlertSeverity.APPROACHING -> "approaching limit"
                    else -> "usage alert"
                })

                withStyle(style = SpanStyle(color = TextSecondary, fontSize = 12.sp)) {
                    append("\n(${alert.usage} / ${alert.limit})")
                }
            }

            Text(
                text = alertMessage,
                color = TextPrimary,
                fontFamily = Poppins,
                fontSize = 14.sp,
            )

            Text(
                text = alert.timeStamp ?: "",
                color = TextSecondary,
                fontFamily = Poppins,
                fontSize = 12.sp
            )
        }

        // Status dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    if (alert.alertType == AlertSeverity.EXCEEDED) AlertPrimary else WarningPrimary)
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    ZenithTheme {
        HomeScreenContent(
            state = HomeUiState(
                isLoading = false,
                overview = UsageOverview(
                    totalUsage = "3h 30m",
                    currentLimit = "4h",
                    monitoredAppsCount = 5,
                    exceededLimitsCount = 2,
                    deviceLimit = "4h"
                ),

                bedtime = BedtimeInfo(
                    bedtimeStatus = true,
                    startTime = "10:00 PM",
                    endTime = "6:30 AM"
                ),

                alerts = listOf(
                    HomeAlert(
                        appName = "YouTube",
                        alertType = AlertSeverity.EXCEEDED,
                        usage = "1h 12m",
                        limit = "1h",
                        timeStamp = "8 min ago"
                    ),

                    HomeAlert(
                        appName = "Instagram",
                        alertType = AlertSeverity.APPROACHING,
                        usage = "1h 30m",
                        limit = "2h",
                        timeStamp = "19 min ago"
                    )
                )
            )
        )
    }
}