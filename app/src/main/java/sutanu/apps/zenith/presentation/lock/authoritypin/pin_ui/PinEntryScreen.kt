package sutanu.apps.zenith.presentation.lock.authoritypin.pin_ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sutanu.apps.zenith.R
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.data.repository_impl.AuthRepositoryImpl
import sutanu.apps.zenith.domain.usecase.security.ValidatePinUseCase
import sutanu.apps.zenith.presentation.lock.authoritypin.PinViewModel
import sutanu.apps.zenith.presentation.ui.theme.AlertPrimary
import sutanu.apps.zenith.presentation.ui.theme.BackgroundPrimary
import sutanu.apps.zenith.presentation.ui.theme.ControlDark
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.InputBg
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.TextPrimary
import sutanu.apps.zenith.presentation.ui.theme.TextSecondary
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@Composable
fun PinEntryScreen(
    viewModel: PinViewModel = hiltViewModel(),
    onPinSuccess: () -> Unit = {},
    customHeaderSubtitleText: String? = null
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onPinSuccess()
        }
    }

    // Shake animation for error feedback
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(state.isError) {
        if (state.isError) {
            val targetOffsets = listOf(-6f, 6f, -4f, 4f, -2f, 2f, 0f)
            for (offset in targetOffsets) {
                shakeOffset.animateTo(
                    targetValue = offset,
                    animationSpec = tween(durationMillis = 30)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // LOGO + Headline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF112950)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_zenith_shield),
                    contentDescription = "Zenith Logo",
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Zenith",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            )

            Text(
                text = if (state.isError && state.errorMessage != null) {
                    state.errorMessage!!
                } else {
                    customHeaderSubtitleText ?: state.headerSubtitleText
                },
                color = if (state.isError) AlertPrimary else TextSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(38.dp))

            // 6 digit progress indicator
            Row(
                modifier = Modifier.offset(x = shakeOffset.value.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val isFilled = index < state.enteredPin.length
                    val digitChar = if (isFilled) state.enteredPin[index].toString() else ""

                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isPinVisible && isFilled) {
                            Text(
                                text = digitChar,
                                color = if (state.isError) AlertPrimary.copy(alpha = 0.5f) else InfoPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Poppins
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            state.isError -> AlertPrimary.copy(alpha = 0.5f)
                                            isFilled -> InfoPrimary
                                            else -> ControlDark
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Grid panel for PIN
        Column(
            modifier = Modifier.padding(bottom = 24.dp),
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
                            isPinVisible = state.isPinVisible,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (key) {
                                    "delete" -> viewModel.onDeleteClick()
                                    "visibility" -> viewModel.togglePinVisibility()
                                    else -> viewModel.onKeyClick(key)
                                }
                            }
                        )
                    }
                }
            }
        }

        //Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
fun KeyPadCell(
    key: String,
    isPinVisible: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(InputBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when(key) {
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

@Preview(showBackground = true, device = "id:pixel_7", showSystemUi = true)
@Composable
private fun PinEntryPadPreview() {
    val context = LocalContext.current
    val repository = AuthRepositoryImpl(AuthPreferences(context), context)
    ZenithTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary)) {
            PinEntryScreen (
                PinViewModel(
                    repository,
                    ValidatePinUseCase(repository)
                )
            )
        }
    }
}