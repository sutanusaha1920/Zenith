package sutanu.apps.zenith.presentation.lock.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun LockScreenContent(
    appName: String,
    onOverrideClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary.copy(alpha = 0.90f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Alert Graphic Container
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AlertPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Time expired Barrier",
                    tint = AlertPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Title Header
            Text(
                text = "Time's Up!",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            Text(
                text = "Your daily screen time limit has been reached.",
                color = TextPrimary,
                fontSize = 16.sp,
                fontFamily = Poppins,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$appName is currently blocked. Time limit exceeded.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Override Button
            Button(
                onClick = onOverrideClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InfoPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Override with PIN",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Contact your parent or guardian to override the limit.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontFamily = Poppins
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun LockScreenContentPreview() {
    ZenithTheme(darkTheme = true) {
        LockScreenContent("Instagram", onOverrideClick = {})
    }
}