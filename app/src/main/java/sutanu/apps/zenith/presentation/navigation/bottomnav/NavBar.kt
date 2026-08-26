package sutanu.apps.zenith.presentation.navigation.bottomnav

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import sutanu.apps.zenith.R
import sutanu.apps.zenith.presentation.navigation.NavRoutes
import sutanu.apps.zenith.presentation.ui.theme.InfoPrimary
import sutanu.apps.zenith.presentation.ui.theme.Poppins
import sutanu.apps.zenith.presentation.ui.theme.SurfacePrimary

@Composable
fun NavBar(
    navController: NavHostController
) {
    val items = listOf(
        NavItem(NavRoutes.HOME, icon = painterResource(R.drawable.ic_home_dashboard)),
        NavItem(NavRoutes.TIMER, icon = painterResource(R.drawable.ic_timer)),
        NavItem(NavRoutes.BEDTIME, icon = painterResource(R.drawable.ic_bedtime_moon_off)),
        NavItem(NavRoutes.SOS, icon = painterResource(R.drawable.ic_sos_location)),
        NavItem(NavRoutes.MONITOR, icon = painterResource(R.drawable.ic_pin_visibility_on)),
        NavItem(NavRoutes.SETTINGS, icon = painterResource(R.drawable.ic_settings))
        )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(color = SurfacePrimary)
    ) {
        val itemWidth = this.maxWidth / items.size
        val selectedIndex = items.indexOfFirst { it.route == currentRoute }

        val indicatorOffset by animateFloatAsState(
            targetValue = if (selectedIndex != -1) selectedIndex.toFloat() else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "indicator"
        )

        Box(
            modifier = Modifier
                .offset(x = (itemWidth * indicatorOffset) + (itemWidth / 2) - 25.dp)
                .width(50.dp)
                .height(2.dp)
                .background(InfoPrimary, RoundedCornerShape(2.dp))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.route == currentRoute

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(
                                        navController.graph
                                            .findStartDestination().id
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Icon(
                        painter = item.icon,
                        contentDescription = item.route,
                        tint = if (isSelected) InfoPrimary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.route.replaceFirstChar { it.uppercase() },
                        color = if (isSelected) InfoPrimary else Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}