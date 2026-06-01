package sutanu.apps.zenith.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sutanu.apps.zenith.R

// Custom Font Family
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

/**
 * Zenith Custom Typography
 * Used for specific app styles not covered by standard Material 3 slots.
 */
@Immutable
data class ZenithTypography(
    val headlineLarge: TextStyle = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val bodyMedium: TextStyle = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    val labelButton: TextStyle = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

/**
 * Layout Spacing
 */
@Immutable
data class ZenithSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
)

// Default instances for internal mapping and CompositionLocal defaults
val DefaultZenithTypography = ZenithTypography()
val DefaultZenithSpacing = ZenithSpacing()

/**
 * Material 3 Typography Configuration
 * Maps custom Zenith styles to Material 3 typography slots.
 */
val MaterialTypography = Typography(
    headlineLarge = DefaultZenithTypography.headlineLarge,
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = DefaultZenithTypography.bodyMedium,
    labelLarge = DefaultZenithTypography.labelButton, // Map button style to labelLarge
    
    /* Optional: Map more styles for consistency */
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )
)
