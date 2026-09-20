package sutanu.apps.zenith.presentation.screen_timer.app_timer.ui

import android.graphics.drawable.Drawable
import java.util.Locale
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sutanu.apps.zenith.R
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.model.AppTimer
import sutanu.apps.zenith.presentation.screen_timer.app_timer.AppTimerViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.OuterCardStrokePrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import sutanu.apps.zenith.domain.model.AppInfo
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun AppTimerScreen(viewModel: AppTimerViewModel) {
    val state by viewModel.uiState.collectAsState()

    AppTimerContent(
        state = state,
        onAddLimitClick = { viewModel.setAddLimitVisible(true) },
        onDeleteLimit = { viewModel.deleteLimit(it) },
        onSelectApp = { viewModel.selectApp(it) },
        onUpdateDraftSlider = { viewModel.updateDraftSlider(it) },
        onApplyLimit = { viewModel.applyLimit() },
        onDismissDialog = { viewModel.setAddLimitVisible(false) }
    )
}

@Composable
fun AppTimerContent(
    state: AppTimer,
    onAddLimitClick: () -> Unit,
    onDeleteLimit: (String) -> Unit,
    onSelectApp: (AppInfo) -> Unit,
    onUpdateDraftSlider: (Float) -> Unit,
    onApplyLimit: () -> Unit,
    onDismissDialog: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Individual App Screen Limits",
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onAddLimitClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Limit",
                    tint = InfoPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Inline Add App Limit Section with AnimatedVisibility
        AnimatedVisibility(
            visible = state.showAddLimitSection,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                AddAppLimitCard(
                    state = state,
                    onSelectApp = onSelectApp,
                    onUpdateDraftSlider = onUpdateDraftSlider,
                    onApplyLimit = onApplyLimit,
                    onCancel = onDismissDialog
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (!state.showAddLimitSection) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Active app limits list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.individualLimits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active screen limits",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Poppins
                    )
                }
            } else {
                state.individualLimits.forEach { limit ->
                    val matchingApp = state.installedAppsList.find { it.packageName == limit.packageName }
                    AppLimitRowItem(
                        limit = limit,
                        icon = matchingApp?.icon,
                        onDelete = { onDeleteLimit(limit.packageName) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppLimitCard(
    state: AppTimer,
    onSelectApp: (AppInfo) -> Unit,
    onUpdateDraftSlider: (Float) -> Unit,
    onApplyLimit: () -> Unit,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val handleCancel = {
        expanded = false
        searchQuery = ""
        onCancel()
    }

    val filteredApps = remember(state.installedAppsList, searchQuery) {
        if (searchQuery.isBlank()) {
            state.installedAppsList
        } else {
            state.installedAppsList.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OuterCardStrokePrimary, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title & Close Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add App Limit",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = handleCancel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Installed app selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Application",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(InputBg)
                        .border(
                            width = 1.dp,
                            color = if (expanded) InfoPrimary else OuterCardStrokePrimary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.selectedAppToLimit?.let { app ->
                            AsyncImage(
                                model = app.icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Text(
                            text = state.selectedAppToLimit?.appName ?: "Tap to choose app",
                            color = if (state.selectedAppToLimit != null) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = Poppins
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Toggle app selector",
                        tint = TextSecondary,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }

                if (expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(InputBg)
                            .border(1.dp, OuterCardStrokePrimary, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Search app...",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    fontFamily = Poppins
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = SurfacePrimary,
                                unfocusedContainerColor = SurfacePrimary,
                                focusedBorderColor = InfoPrimary,
                                unfocusedBorderColor = OuterCardStrokePrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        if (filteredApps.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No apps found",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    fontFamily = Poppins
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = filteredApps,
                                    key = { it.packageName }
                                ) { app ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                onSelectApp(app)
                                                expanded = false
                                                searchQuery = ""
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (app.icon != null) {
                                            AsyncImage(
                                                model = app.icon,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }
                                        Text(
                                            text = app.appName,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontFamily = Poppins,
                                            fontWeight = if (state.selectedAppToLimit?.packageName == app.packageName) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Time Limit Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Limit",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${"%.1f".format(Locale.US, state.draftLimitHours).replace(".0", "")}h",
                    color = InfoPrimary,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = state.draftLimitHours,
                onValueChange = onUpdateDraftSlider,
                valueRange = 0.5f..6f,
                steps = 10,
                colors = SliderDefaults.colors(
                    activeTrackColor = InfoPrimary,
                    inactiveTrackColor = ControlDark
                ),
                modifier = Modifier.fillMaxWidth(),
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
                        drawStopIndicator = { },
                        drawTick = { _, _ -> }
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0.5h",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = Poppins
                )

                Text(
                    text = "6h",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = Poppins
                )
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = handleCancel) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary,
                        fontFamily = Poppins
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onApplyLimit,
                    enabled = state.selectedAppToLimit != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InfoPrimary,
                        disabledContainerColor = ControlDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Apply Limit",
                        color = TextPrimary,
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}


@Composable
fun AppLimitRowItem(
    limit: AppLimitEntity,
    icon: Drawable?,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfacePrimary)
            .border(1.dp, OuterCardStrokePrimary, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    error = painterResource(R.drawable.ic_android_logo)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = limit.appName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold
                )

                val progress = if (limit.dailyLimitMinutes > 0) {
                    limit.dailyMinutesUsed.toFloat() / limit.dailyLimitMinutes
                } else 0f

                LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = InfoPrimary,
                trackColor = ControlDark,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                Text(
                    text = "${limit.dailyMinutesUsed / 60}h ${limit.dailyMinutesUsed % 60}m used",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "limit: ${"%.1f".format(Locale.US, limit.dailyLimitMinutes / 60f).replace(".0", "")}h",
                color = TextSecondary,
                fontSize = 14.sp,
                fontFamily = Poppins,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = onDelete,
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AlertPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = TextSecondary
                )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
fun AppTimerPreview() {
    ZenithTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AppTimerContent(
                state = AppTimer(
                    individualLimits = listOf(
                        AppLimitEntity(
                            packageName = "com.android.chrome",
                            appName = "Chrome",
                            dailyLimitMinutes = 120
                        ),
                        AppLimitEntity(
                            packageName = "com.google.android.youtube",
                            appName = "YouTube",
                            dailyLimitMinutes = 60
                        )
                    ),
                    installedAppsList = listOf(
                        AppInfo("com.android.chrome", "Chrome", null),
                        AppInfo("com.google.android.youtube", "YouTube", null),
                        AppInfo("com.whatsapp", "WhatsApp", null)
                    )
                ),
                onAddLimitClick = {},
                onDeleteLimit = {},
                onSelectApp = {},
                onUpdateDraftSlider = {},
                onApplyLimit = {},
                onDismissDialog = {}
            )
        }
    }
}
