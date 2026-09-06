package sutanu.apps.zenith.presentation.lock.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import sutanu.apps.zenith.presentation.lock.authoritypin.pin_ui.PinEntryScreen
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme

@AndroidEntryPoint
class LockScreenOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            // Intentionally left blank to override and block the back button
        }

        val blockedAppName = intent.getStringExtra("EXTRA_BLOCKED_APP_NAME") ?: "This App"

        setContent {
            ZenithTheme {
                var showPinEntry by rememberSaveable { mutableStateOf(false) }

                BackHandler(enabled = showPinEntry) {
                    showPinEntry = false
                }

                AnimatedContent(
                    targetState = showPinEntry,
                    label = "LockOverlayTransition"
                ) { isPinVisible ->
                    if (isPinVisible) {
                        PinEntryScreen(
                            customHeaderSubtitleText = "Enter your PIN to unlock $blockedAppName",
                            onPinSuccess = {
                                finish()
                            }
                        )
                    } else {
                        LockScreenContent(
                            appName = blockedAppName,
                            onOverrideClick = {
                                showPinEntry = true
                            }
                        )
                    }
                }
            }
        }
    }
}