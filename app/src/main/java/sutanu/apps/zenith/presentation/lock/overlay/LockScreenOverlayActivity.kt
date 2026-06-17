package sutanu.apps.zenith.presentation.lock.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
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
            ZenithTheme() {
                LockScreenContent(blockedAppName,
                    onOverrideClick = {
                        // TODO: 6 digit pin screen
                    }
                )
            }
        }
    }
}