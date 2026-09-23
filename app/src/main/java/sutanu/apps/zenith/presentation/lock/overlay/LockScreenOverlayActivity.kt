package sutanu.apps.zenith.presentation.lock.overlay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sutanu.apps.zenith.core.util.BedtimeUtils
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import sutanu.apps.zenith.presentation.lock.authoritypin.pin_ui.PinEntryScreen
import sutanu.apps.zenith.presentation.ui.theme.ZenithTheme
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class LockScreenOverlayActivity : ComponentActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    @Inject
    lateinit var appTimerRepository: AppTimerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            // Intentionally left blank to override and block the back button
        }

        val blockedAppName = intent.getStringExtra("EXTRA_BLOCKED_APP_NAME") ?: "This App"
        val blockedPackage = intent.getStringExtra("EXTRA_BLOCKED_PACKAGE") ?: ""
        val isBedtime = blockedAppName.contains("Bedtime", ignoreCase = true)

        setContent {
            ZenithTheme {
                var showPinEntry by rememberSaveable { mutableStateOf(false) }

                // Auto-dismiss overlay when bedtime window ends or is disabled
                if (isBedtime) {
                    LaunchedEffect(Unit) {
                        while (isActive) {
                            val isEnabled = authPreferences.isBedtimeEnabled.first()
                            val start = authPreferences.bedtimeStartTime.first()
                            val end = authPreferences.bedtimeEndTime.first()

                            val inBedtime = isEnabled && BedtimeUtils.isCurrentTimeInBedtimeWindow(start, end)
                            if (!inBedtime) {
                                finish() // Auto unlock screen!
                                break
                            }
                            delay(5.seconds)
                        }
                    }
                }

                BackHandler(enabled = showPinEntry) {
                    showPinEntry = false
                }

                val pinSubtitleText = if (isBedtime) {
                    "Enter your PIN to unlock the app"
                } else {
                    "Enter your PIN to unlock $blockedAppName"
                }

                AnimatedContent(
                    targetState = showPinEntry,
                    label = "LockOverlayTransition"
                ) { isPinVisible ->
                    if (isPinVisible) {
                        PinEntryScreen(
                            customHeaderSubtitleText = pinSubtitleText,
                            onPinSuccess = {
                                lifecycleScope.launch {
                                    if (isBedtime) {
                                        Log.d("ZenithSecurity", "Bedtime mode unlocked with PIN")
                                    } else if (blockedAppName.equals("Device", ignoreCase = true) || blockedAppName.contains("Device", ignoreCase = true)) {
                                        val extMins = authPreferences.deviceOverrideExtensionMinutes.first()
                                        authPreferences.grantDeviceOverrideExtension(extMins)
                                        Log.d("ZenithSecurity", "Granted $extMins mins Device PIN override extension")
                                    } else {
                                        val extMins = authPreferences.appOverrideExtensionMinutes.first()
                                        if (blockedPackage.isNotEmpty()) {
                                            appTimerRepository.grantAppOverride(blockedPackage, extMins)
                                            Log.d("ZenithSecurity", "Granted $extMins mins App PIN override extension for $blockedPackage")
                                        } else {
                                            Log.w("ZenithSecurity", "blockedPackage was empty! Cannot grant app override.")
                                        }
                                    }
                                    finish()
                                }
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
