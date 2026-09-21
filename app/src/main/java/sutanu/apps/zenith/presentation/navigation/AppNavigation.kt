package sutanu.apps.zenith.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import sutanu.apps.zenith.presentation.bedtime.bedtime_ui.BedtimeScreen
import sutanu.apps.zenith.presentation.home.ui.HomeScreen
import sutanu.apps.zenith.presentation.lock.authoritypin.pin_ui.PinEntryScreen
import sutanu.apps.zenith.presentation.monitor.monitor_ui.MonitorScreen
import sutanu.apps.zenith.presentation.navigation.bottomnav.NavBar
import sutanu.apps.zenith.presentation.screen_timer.ScreenTimerContainer
import sutanu.apps.zenith.presentation.screen_timer.app_timer.ui.AppTimerScreen
import sutanu.apps.zenith.presentation.screen_timer.device_timer.ui.DeviceTimerScreen
import sutanu.apps.zenith.presentation.settings.settings_ui.ChangePinScreen
import sutanu.apps.zenith.presentation.settings.settings_ui.DeletionProtectionScreen
import sutanu.apps.zenith.presentation.settings.settings_ui.SettingsScreen
import sutanu.apps.zenith.presentation.sos.sos_ui.SosScreen
import sutanu.apps.zenith.presentation.splash.SplashScreen

object HomeNav {
    const val ROUTE = "home"
}

object TimerTab {
    const val ENTIRE_DEVICE = "entire_device"
    const val INDIVIDUAL_APPS = "individual_apps"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null && currentRoute != NavRoutes.SPLASH && currentRoute in listOf(
        HomeNav.ROUTE,
        NavRoutes.TIMER,
        TimerTab.ENTIRE_DEVICE,
        TimerTab.INDIVIDUAL_APPS,
        NavRoutes.BEDTIME,
        NavRoutes.SOS,
        NavRoutes.MONITOR,
        NavRoutes.SETTINGS
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
            ) {
                NavBar(navController = navController)
            }
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SPLASH,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) paddingValues.calculateBottomPadding() else 0.dp,
                top = paddingValues.calculateTopPadding()
            )
        ) {
            composable(NavRoutes.SPLASH) {
                SplashScreen(onNextScreen = {
                    navController.navigate(NavRoutes.PIN) {
                        popUpTo(NavRoutes.SPLASH) {
                            inclusive = true
                        }
                    }
                })
            }

            composable(NavRoutes.PIN) {
                PinEntryScreen(
                    onPinSuccess = {
                        navController.navigate(HomeNav.ROUTE) {
                            popUpTo(NavRoutes.PIN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(NavRoutes.HOME) {
                HomeScreen(viewModel = hiltViewModel())
            }

            composable(NavRoutes.TIMER) {
                ScreenTimerContainer(
                    deviceTimerViewModel = hiltViewModel(),
                    appTimerViewModel = hiltViewModel()
                )
            }

            composable("entire_device") {
                DeviceTimerScreen(viewModel = hiltViewModel())
            }

            composable("individual_apps") {
                AppTimerScreen(viewModel = hiltViewModel())
            }

            composable(NavRoutes.BEDTIME) {
                BedtimeScreen(viewModel = hiltViewModel())
            }

            composable(NavRoutes.SOS) {
                SosScreen(viewModel = hiltViewModel())
            }

            composable(NavRoutes.MONITOR) {
                MonitorScreen(viewModel = hiltViewModel())
            }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onNavigateToChangePin = {
                        navController.navigate(NavRoutes.CHANGE_PIN)
                    },
                    onNavigateToLockScreen = {
                        navController.navigate(NavRoutes.PIN)
                    },
                    onNavigateToDeletionProtection = {
                        navController.navigate(NavRoutes.DELETE_PROTECTION)
                    }
                )
            }

            composable(NavRoutes.CHANGE_PIN) {
                ChangePinScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(NavRoutes.DELETE_PROTECTION) {
                DeletionProtectionScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
