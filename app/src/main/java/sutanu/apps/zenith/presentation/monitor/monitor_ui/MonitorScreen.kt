package sutanu.apps.zenith.presentation.monitor.monitor_ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import sutanu.apps.zenith.R
import sutanu.apps.zenith.domain.model.AppLaunchInfo
import sutanu.apps.zenith.domain.model.DailyUsage
import sutanu.apps.zenith.domain.model.UsageSeverity
import sutanu.apps.zenith.presentation.monitor.MonitorUiState
import sutanu.apps.zenith.presentation.monitor.MonitorViewModel
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

@Composable
fun MonitorScreen(
    viewModel: MonitorViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MonitorScreenContent(
        uiState = uiState
    )
}

@Composable
fun MonitorScreenContent(
    uiState: MonitorUiState
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header with Toggle Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Accessibility Monitor",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "App launch & usage tracking",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }

            Switch(
                checked = uiState.isAccessibilityActive,
                onCheckedChange = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TextPrimary,
                    checkedTrackColor = InfoPrimary,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceSecondary
                )
            )
        }

        // Card 1: Accessibility Service Status
        AccessibilityStatusCard(
            isAccessibilityActive = uiState.isAccessibilityActive
        )

        // Card 2: Weekly Usage Bar Chart with Growth Animation
        WeeklyUsageCard(
            weeklyUsage = uiState.weeklyUsage
        )

        // Card 3: Recent App Launches List
        RecentAppLaunchesCard(
            recentAppLaunches = uiState.recentAppLaunches
        )

        // Card 4: Accessibility Disclaimer Card
        AccessibilityDisclaimerCard()
    }
}

@Composable
fun AccessibilityStatusCard(
    isAccessibilityActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        border = BorderStroke(1.dp, OuterCardStrokePrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconRes = if (isAccessibilityActive) R.drawable.ic_check_circle else R.drawable.ic_alert_triangle
            val iconTint = if (isAccessibilityActive) InfoPrimary else WarningPrimary

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconTint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isAccessibilityActive) "Accessibility Service Active" else "Accessibility Service Required",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAccessibilityActive) "Monitoring app launches and foreground activity" else "Settings ➔ Accessibility ➔ Downloaded apps ➔ Zenith ➔ Turn ON \"Use Zenith\" / Accessibility",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
fun WeeklyUsageCard(
    weeklyUsage: List<DailyUsage>
) {
    var isAnimated by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<DailyUsage?>(null) }

    LaunchedEffect(weeklyUsage) {
        isAnimated = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "weeklyBarGrowth"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        border = BorderStroke(1.dp, OuterCardStrokePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pulse),
                        contentDescription = null,
                        tint = InfoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Weekly Usage",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins
                    )
                }
                Text(
                    text = "This week",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Area with Interactive Selection & Floating Tooltip Overlay
            val maxUsage = weeklyUsage.maxOfOrNull { it.usageMinutes }?.coerceAtLeast(1) ?: 1

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Chart Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyUsage.forEach { item ->
                        val isSelected = selectedDay?.dayLabel == item.dayLabel
                        val targetFraction = (item.usageMinutes.toFloat() / maxUsage.toFloat()).coerceIn(0.08f, 1.0f)
                        val barColor = when (item.usageSeverity) {
                            UsageSeverity.NORMAL -> InfoPrimary
                            UsageSeverity.HIGH -> WarningPrimary
                            UsageSeverity.EXCESSIVE -> AlertPrimary
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedDay = if (isSelected) null else item
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isSelected) Color.White.copy(alpha = 0.22f) else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .fillMaxHeight(targetFraction)
                                        .graphicsLayer {
                                            scaleY = animatedProgress
                                            transformOrigin = TransformOrigin(0.5f, 1f)
                                        }
                                        .background(
                                            color = barColor,
                                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.dayLabel,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                // 2. Floating Tooltip Overlay Card
                selectedDay?.let { selected ->
                    val selectedIndex = weeklyUsage.indexOfFirst { it.dayLabel == selected.dayLabel }
                    if (selectedIndex != -1) {
                        val isRightHalf = selectedIndex >= 4

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .align(if (isRightHalf) Alignment.CenterStart else Alignment.CenterEnd)
                                    .padding(
                                        start = if (!isRightHalf) 0.dp else 12.dp,
                                        end = if (isRightHalf) 0.dp else 12.dp
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
                                border = BorderStroke(1.dp, OuterCardStrokePrimary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = selected.dayLabel,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = Poppins
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Usage : ${formatUsageHours(selected.usageMinutes)}",
                                        color = TextSecondary,
                                        fontSize = 14.sp,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = InfoPrimary, label = "Normal")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = WarningPrimary, label = "High")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = AlertPrimary, label = "Excessive")
            }
        }
    }
}

private fun formatUsageHours(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return if (hours > 0) {
        "${hours}h ${remainingMinutes}m"
    } else {
        "${remainingMinutes}m"
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = Poppins
        )
    }
}

@Composable
fun RecentAppLaunchesCard(
    recentAppLaunches: List<AppLaunchInfo>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        border = BorderStroke(1.dp, OuterCardStrokePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pin_visibility_on),
                    contentDescription = null,
                    tint = InfoPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recent App Launches",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recentAppLaunches.isEmpty()) {
                Text(
                    text = "No recent app launches recorded",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                recentAppLaunches.forEachIndexed { index, app ->
                    AppLaunchItem(app = app)

                    if (index < recentAppLaunches.lastIndex) {
                        HorizontalDivider(
                            color = OuterCardStrokePrimary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppLaunchItem(app: AppLaunchInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon
        if (app.icon != null) {
            Image(
                painter = rememberDrawablePainter(drawable = app.icon),
                contentDescription = app.appName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceSecondary, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_android_logo),
                    contentDescription = null,
                    tint = InfoPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // App Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = app.appName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )

                if (app.isLimitExceeded) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = AlertPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = AlertPrimary.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Limit exceeded",
                            color = AlertPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Poppins
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${app.lastLaunchedText} · ${app.usageText}",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun AccessibilityDisclaimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarningPrimary.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, WarningPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_zenith_shield),
                contentDescription = null,
                tint = WarningPrimary,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Accessibility Services require explicit user permission on Android. Screen content inspection is used only for policy enforcement and is not transmitted off-device.",
                color = WarningPrimary,
                fontSize = 12.sp,
                fontFamily = Poppins,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun MonitorScreenPreview() {
    ZenithTheme {
        MonitorScreenContent(
            uiState = MonitorUiState()
        )
    }
}