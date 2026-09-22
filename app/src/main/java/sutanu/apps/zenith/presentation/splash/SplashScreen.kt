package sutanu.apps.zenith.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import sutanu.apps.zenith.R
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onNextScreen: () -> Unit) {
    val alpha = remember { Animatable(0f) }

    val backgroundBrush = Brush.linearGradient(
        listOf(
            Color(0xFF162240),
            Color(0xFF1F3358)
        )
    )

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1000.milliseconds)
        onNextScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .drawBehind {
                        val color = Color(0xFF3B82F6)
                        val shadowColor = color.copy(alpha = 0.2f).toArgb()
                        val transparentColor = color.copy(alpha = 0f).toArgb()
                        
                        drawIntoCanvas { canvas ->
                            val paint = Paint()
                            val frameworkPaint = paint.asFrameworkPaint()
                            frameworkPaint.color = transparentColor
                            frameworkPaint.setShadowLayer(
                                20.dp.toPx(),
                                0f, 
                                2.dp.toPx(),
                                shadowColor
                            )
                            canvas.drawRoundRect(
                                0f,
                                0f,
                                this.size.width,
                                this.size.height,
                                32.dp.toPx(),
                                32.dp.toPx(),
                                paint
                            )
                        }
                    }
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1C3A6B))
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF2D5A9E).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zenith_shield),
                    contentDescription = "Zenith Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Name
            Text(
                text = "ZENITH",
                fontFamily = Poppins,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "Your shield in the connected world",
                fontFamily = Poppins,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    ZenithTheme {
        SplashScreen(onNextScreen = {})
    }
}