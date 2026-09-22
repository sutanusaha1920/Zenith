package sutanu.apps.zenith.presentation.settings.settings_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sutanu.apps.zenith.R
import sutanu.apps.zenith.presentation.settings.SettingsViewModel
import sutanu.apps.zenith.presentation.settings.uistate.SettingsUiState
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary
import sutanu.apps.zenith.presentation.ui.theme.SurfaceSecondary
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToChangePin: () -> Unit,
    onNavigateToLockScreen: () -> Unit,
    onNavigateToDeletionProtection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        onNavigateToChangePin = onNavigateToChangePin,
        onNavigateToLockScreen = onNavigateToLockScreen,
        onNavigateToDeletionProtection = onNavigateToDeletionProtection,
        onThemeSelected = { mode -> viewModel.setThemeMode(mode) }
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onNavigateToChangePin: () -> Unit,
    onNavigateToLockScreen: () -> Unit,
    onNavigateToDeletionProtection: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = Poppins
            )

            Text(
                text = "App configuration & security",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
        ) {
            Column {
                Text(
                    text = "SECURITY",
                    fontSize = 16.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                SettingsItem(
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_key),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    iconBg = InfoPrimary.copy(alpha = 0.15f),
                    title = "Change Authorization PIN",
                    subtitle = "Update your app unlock code",
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    onClick = onNavigateToChangePin
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                SettingsItem(
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_lock),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(InfoPrimary)
                        )
                    },
                    iconBg = InfoPrimary.copy(alpha = 0.15f),
                    title = "Lock App",
                    subtitle = "Return to PIN screen",
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    onClick = onNavigateToLockScreen
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                SettingsItem(
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(AlertPrimary)
                        )
                    },
                    iconBg = AlertPrimary.copy(alpha = 0.4f),
                    title = "App Deletion Protection",
                    subtitle = "Require PIN to uninstall this app",
                    trailing = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    onClick = onNavigateToDeletionProtection
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Appearance section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
        ) {
            Column {
                Text(
                    text = "APPEARANCE",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                var isThemeExpanded by remember { mutableStateOf(false) }
                val arrowRotation by animateFloatAsState(
                    targetValue = if (isThemeExpanded) 90f else 0f,
                    label = "ArrowRotation"
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsItem(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_theme_palette),
                                contentDescription = null,
                                tint = InfoPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        iconBg = InfoPrimary.copy(alpha = 0.15f),
                        title = "Theme",
                        subtitle = when (uiState.themeMode) {
                            "light" -> "Light"
                            "dark" -> "Dark"
                            else -> "System (Device Theme)"
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.rotate(arrowRotation)
                            )
                        },
                        onClick = { isThemeExpanded = !isThemeExpanded }
                    )

                    AnimatedVisibility(
                        visible = isThemeExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 8.dp),
                                thickness = 0.20.dp,
                                color = TextSecondary.copy(alpha = 0.5f)
                            )

                            ThemeOptionItem(
                                text = "Light",
                                isSelected = uiState.themeMode == "light",
                                onClick = {
                                    onThemeSelected("light")
                                    isThemeExpanded = false
                                }
                            )

                            ThemeOptionItem(
                                text = "Dark",
                                isSelected = uiState.themeMode == "dark",
                                onClick = {
                                    onThemeSelected("dark")
                                    isThemeExpanded = false
                                }
                            )

                            ThemeOptionItem(
                                text = "System (Device Theme)",
                                isSelected = uiState.themeMode == "system",
                                onClick = {
                                    onThemeSelected("system")
                                    isThemeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
        ) {
            Column {
                Text(
                    text = "ABOUT",
                    fontFamily = Poppins,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                SettingsItem(
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_zenith_shield),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    iconBg = InfoPrimary.copy(alpha = 0.15f),
                    title = "Zenith",
                    subtitle = "Version 1.0.0 · Build 2026.06.10",
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    thickness = 0.20.dp,
                    color = TextSecondary.copy(alpha = 0.9f)
                )

                SettingsItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_alert_circle),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = InfoPrimary
                        )
                    },
                    iconBg = InfoPrimary.copy(alpha = 0.15f),
                    title = "App Protection Status",
                    subtitle = if (uiState.securityStatus.isUninstallProtected) "Active · Protected" else "Inactive",
                    trailing = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape((12.dp)))
                                .background(SurfaceSecondary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text(
                                text = if (uiState.securityStatus.isUninstallProtected) "Active" else "Inactive",
                                color = if (uiState.securityStatus.isUninstallProtected) InfoPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePrimary)
        ) {
            Text(
                text = "This app is protected by Zenith Accessibility Shield. The authorization PIN is required to uninstall or disable this application, preventing unauthorized removal by the device user.",
                color = TextSecondary,
                fontFamily = Poppins,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ThemeOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = if (isSelected) InfoPrimary else TextPrimary,
            fontFamily = Poppins,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(InfoPrimary)
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: @Composable () -> Unit,
    iconBg: Color = SurfaceSecondary,
    title: String,
    subtitle: String,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            // Icon to be added
            icon()
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            //Item Title
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            //Item Subtitle
            Text(
                text = subtitle,
                color = TextSecondary,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    ZenithTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(),
            onNavigateToChangePin = {},
            onNavigateToLockScreen = {},
            onNavigateToDeletionProtection = {},
            onThemeSelected = {}
        )
    }
}