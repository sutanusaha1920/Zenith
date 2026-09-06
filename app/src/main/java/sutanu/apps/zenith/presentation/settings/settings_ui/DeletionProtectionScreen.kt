package sutanu.apps.zenith.presentation.settings.settings_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sutanu.apps.zenith.R
import sutanu.apps.zenith.presentation.settings.DeletionProtectionViewModel
import sutanu.apps.zenith.presentation.settings.uistate.DeletionProtectionUiState
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.SurfaceSecondary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary

@Composable
fun DeletionProtectionScreen(
    viewModel: DeletionProtectionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DeletionProtectionContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onToggleClicked = viewModel::onToggleClicked,
        onKeyClicked = viewModel::onKeyClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onPinVisibilityClicked = viewModel::onPinVisibilityClicked,
        onCancelClicked = viewModel::onCancelClicked
    )
}

@Composable
fun DeletionProtectionContent(
    uiState: DeletionProtectionUiState,
    onNavigateBack: () -> Unit,
    onToggleClicked: (Boolean) -> Unit,
    onKeyClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onPinVisibilityClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (title and back button)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier.clickable { onNavigateBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = InfoPrimary
                )

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = "Back",
                    color = InfoPrimary,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            Text(
                text = "Deletion Protection",
                color = TextPrimary,
                fontSize = 24.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AlertPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_zenith_shield),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AlertPrimary),
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "App Deletion Protection",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "PIN required to uninstall this app",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Switch(
                        checked = uiState.isDeletionProtectionEnabled,
                        onCheckedChange = { onToggleClicked(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = InfoPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Transparent
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (uiState.isDeletionProtectionEnabled) InfoPrimary.copy(alpha = 0.15f)
                            else AlertPrimary.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        painter = if (uiState.isDeletionProtectionEnabled) {
                            painterResource(R.drawable.ic_check_circle)
                        } else {
                            painterResource(R.drawable.ic_alert_circle)
                        },
                        contentDescription = null,
                        tint = if (uiState.isDeletionProtectionEnabled) {
                            InfoPrimary
                        } else {
                            AlertPrimary
                        },
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (uiState.isDeletionProtectionEnabled) {
                            "Protection is active"
                        } else {
                            "Protection is disabled"
                        },
                        color = if (uiState.isDeletionProtectionEnabled) {
                            InfoPrimary
                        } else {
                            AlertPrimary
                        },
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Numpad
        AnimatedVisibility(
            visible = uiState.showNumpad,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter PIN to disable protection",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Your authorization PIN is required to turn off deletion protection",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val shakeOffset = remember { Animatable(0f) }

                    LaunchedEffect(uiState.isPinError) {
                        if (uiState.isPinError) {
                            val targetOffsets = listOf(-16f, 16f, -12f, 12f, -6f, 6f, 0f)
                            for (offset in targetOffsets) {
                                shakeOffset.animateTo(
                                    targetValue = offset,
                                    animationSpec = tween(durationMillis = 35)
                                )
                            }
                        }
                    }

                    // Dot Indicators
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .offset(x = shakeOffset.value.dp)
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(6) { index ->
                                val isFilled = index < uiState.enteredPin.length

                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                uiState.isPinError -> AlertPrimary
                                                isFilled -> InfoPrimary
                                                else -> SurfaceSecondary
                                            }
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (uiState.isPinError && uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage,
                                color = AlertPrimary,
                                fontSize = 12.sp,
                                fontFamily = Poppins,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val keys = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("visibility", "0", "delete")
                        )

                        keys.forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { key ->
                                    KeyPadCell(
                                        key = key,
                                        onClick = {
                                            when (key) {
                                                "delete" -> onDeleteClicked()
                                                "visibility" -> onPinVisibilityClicked()
                                                else -> onKeyClicked(key)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        isPinVisible = uiState.isPinVisible
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(InputBg)
                                .clickable { onCancelClicked() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                color = TextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                FeatureItem(
                    title = "PIN Required to Uninstall",
                    subtitle = "Anyone attempting to delete this app will be prompted for the authorization PIN."
                )

                FeatureItem(
                    title = "Device Admin Rights",
                    subtitle = "The app holds device administrator privileges, preventing removal without proper authorization."
                )

                FeatureItem(
                    title = "Tamper Detection",
                    subtitle = "Any attempt to disable or force-stop the app is detected and logged."
                )
            }
        }
    }
}

@Composable
fun KeyPadCell(
    key: String,
    onClick: () -> Unit,
    isPinVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(62.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(InputBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "visibility" -> {
                Image(
                    painter = if (! isPinVisible) {
                        painterResource(R.drawable.ic_pin_visibility_on)
                    } else {
                        painterResource(R.drawable.ic_pin_visibility_off)
                    },
                    contentDescription = "Toggle Pin Visibility",
                    modifier = Modifier.size(24.dp)
                )
            }

            "delete" -> {
                Text("⌫", color = TextSecondary, fontSize = 24.sp)
            }

            else -> {
                Text(
                    text = key,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Composable
fun FeatureItem(
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_circle),
                contentDescription = null,
                tint = InfoPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview
@Composable
private fun DeletionProtectionScreenPreview() {
    DeletionProtectionContent(
        uiState = DeletionProtectionUiState(),
        onNavigateBack = {},
        onToggleClicked = {},
        onKeyClicked = {},
        onDeleteClicked = {},
        onPinVisibilityClicked = {},
        onCancelClicked = {}
    )
}