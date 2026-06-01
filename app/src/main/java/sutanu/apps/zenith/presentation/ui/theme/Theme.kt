package sutanu.apps.zenith.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

private val DarkColorScheme = darkColorScheme(
    primary = TextPrimary,
    secondary = SlateSurface,
    tertiary = MintSafe,
    background = ObsidianBackground,
    surface = SlateSurface,
    onPrimary = ObsidianBackground,
    onSecondary = TextPrimary,
    onTertiary = ObsidianBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = TextPrimary,
    secondary = SlateSurface,
    tertiary = MintSafe
)

data class ZenithColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val interactive: Color,
    val alert: Color,
    val safe: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

private val DarkZenithColors = ZenithColors(
    background = ObsidianBackground,
    surface = SlateSurface,
    border = CardStroke,
    interactive = ControlDark,
    alert = CrimsonAlert,
    safe = MintSafe,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary
)

private val LightZenithColors = DarkZenithColors // TODO: Define light colors

private val LocalColors = staticCompositionLocalOf { DarkZenithColors }
private val LocalTypography = staticCompositionLocalOf { DefaultZenithTypography }
private val LocalSpacing = staticCompositionLocalOf { DefaultZenithSpacing }

object ZenithTheme {
    val colors: ZenithColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: ZenithTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val spacing: ZenithSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current
}

@Composable
fun ZenithTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !LocalInspectionMode.current -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val zenithColors = if (darkTheme) DarkZenithColors else LightZenithColors

    CompositionLocalProvider(
        LocalColors provides zenithColors,
        LocalTypography provides DefaultZenithTypography,
        LocalSpacing provides DefaultZenithSpacing
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaterialTypography,
            content = content
        )
    }
}
