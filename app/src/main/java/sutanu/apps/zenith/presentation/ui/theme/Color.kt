package sutanu.apps.zenith.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDarkTheme = staticCompositionLocalOf { true }

// Core Theme Palettes

val BackgroundPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background

val SurfacePrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

val SurfaceSecondary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

val InputBg: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

val OuterCardStrokePrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

val ControlDark: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant


// High-Emphasized System Colors

val AlertPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error

val WarningPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary

val InfoPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary

val MintSafe: Color
    @Composable @ReadOnlyComposable get() = Color(0xFF10B981)

val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant


// Light Color Theme Palette (High Contrast & Clear Separation)

val BackgroundPrimaryLight = Color(0xFFEEF2F6)

val SurfacePrimaryLight = Color(0xFFFFFFFF)

val SurfaceSecondaryLight = Color(0xFFE2E8F0)

val InputBgLight = Color(0xFFE2E8F0)

val OuterCardStrokePrimaryLight = Color(0xFFCBD5E1)

val ControlLight = Color(0xFF94A3B8)

val TextPrimaryLight = Color(0xFF0F172A)

val TextSecondaryLight = Color(0xFF475569)
