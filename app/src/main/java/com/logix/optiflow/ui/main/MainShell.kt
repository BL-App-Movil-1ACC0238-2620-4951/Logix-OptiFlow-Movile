package com.logix.optiflow.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.logix.optiflow.R
import com.logix.optiflow.ui.home.HomeScreen
import com.logix.optiflow.ui.notifications.NotificationsScreen
import com.logix.optiflow.ui.navigation.Routes
import com.logix.optiflow.ui.profile.ProfileScreen
import com.logix.optiflow.ui.search.StoreSearchScreen
import com.logix.optiflow.ui.theme.OptiFlowMuted
import com.logix.optiflow.ui.theme.OptiFlowNavy
import java.util.UUID
import kotlinx.coroutines.launch

object MainRoutes {
    const val HOME = "main/home"
    const val SEARCH = "main/search"
    const val APPOINTMENTS = "main/appointments"
    const val ORDERS = "main/orders"
    const val PROFILE = "main/profile"
    const val NOTIFICATIONS = "main/notifications"
}

@Composable
fun MainShell(
    rootNavController: NavHostController,
    onLogout: () -> Unit,
) {
    val tabNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backStackEntry = tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route ?: MainRoutes.HOME

    fun showMessage(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                BottomNavItem(
                    selected = currentRoute == MainRoutes.HOME || currentRoute == MainRoutes.NOTIFICATIONS,
                    label = stringResource(R.string.nav_home),
                    icon = Icons.Filled.Home,
                    onClick = { navigateTab(tabNavController, MainRoutes.HOME) },
                )
                BottomNavItem(
                    selected = currentRoute == MainRoutes.SEARCH,
                    label = stringResource(R.string.nav_search),
                    icon = Icons.Filled.Visibility,
                    onClick = { navigateTab(tabNavController, MainRoutes.SEARCH) },
                )
                BottomNavItem(
                    selected = currentRoute == MainRoutes.APPOINTMENTS,
                    label = stringResource(R.string.nav_appointments),
                    icon = Icons.Filled.CalendarMonth,
                    onClick = { navigateTab(tabNavController, MainRoutes.APPOINTMENTS) },
                )
                BottomNavItem(
                    selected = currentRoute == MainRoutes.ORDERS,
                    label = stringResource(R.string.nav_orders),
                    icon = Icons.Filled.Inventory2,
                    onClick = { navigateTab(tabNavController, MainRoutes.ORDERS) },
                )
                BottomNavItem(
                    selected = currentRoute == MainRoutes.PROFILE,
                    label = stringResource(R.string.nav_profile),
                    icon = Icons.Filled.Person,
                    onClick = { navigateTab(tabNavController, MainRoutes.PROFILE) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = tabNavController,
            startDestination = MainRoutes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(MainRoutes.HOME) {
                HomeScreen(
                    onViewAllAppointments = {
                        tabNavController.navigate(MainRoutes.APPOINTMENTS) { launchSingleTop = true }
                    },
                    onOpenSearch = {
                        tabNavController.navigate(MainRoutes.SEARCH) { launchSingleTop = true }
                    },
                    onOpenOrders = {
                        tabNavController.navigate(MainRoutes.ORDERS) { launchSingleTop = true }
                    },
                    onOpenNotifications = {
                        tabNavController.navigate(MainRoutes.NOTIFICATIONS) { launchSingleTop = true }
                    },
                    onTryVirtual = {
                        showMessage("Prueba virtual AR — disponible en una próxima versión.")
                    },
                    onShowSnackbar = ::showMessage,
                )
            }
            composable(MainRoutes.NOTIFICATIONS) {
                NotificationsScreen(
                    onBack = { tabNavController.popBackStack() },
                    onOpenSearch = {
                        tabNavController.popBackStack()
                        tabNavController.navigate(MainRoutes.SEARCH) { launchSingleTop = true }
                    },
                )
            }
            composable(MainRoutes.SEARCH) {
                StoreSearchScreen(
                    onOpenAuth = {
                        tabNavController.navigate(MainRoutes.PROFILE) { launchSingleTop = true }
                    },
                    onSelectStore = { storeId: UUID ->
                        rootNavController.navigate(Routes.booking(storeId))
                    },
                )
            }
            composable(MainRoutes.APPOINTMENTS) {
                PlaceholderTabScreen(
                    title = stringResource(R.string.nav_appointments),
                    body = stringResource(R.string.home_appointments_placeholder),
                )
            }
            composable(MainRoutes.ORDERS) {
                PlaceholderTabScreen(
                    title = stringResource(R.string.nav_orders),
                    body = stringResource(R.string.home_orders_placeholder),
                )
            }
            composable(MainRoutes.PROFILE) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

private fun navigateTab(
    tabNavController: NavHostController,
    route: String,
) {
    tabNavController.navigate(route) {
        popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun RowScope.BottomNavItem(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, maxLines = 1) },
        colors =
            NavigationBarItemDefaults.colors(
                selectedIconColor = OptiFlowNavy,
                selectedTextColor = OptiFlowNavy,
                indicatorColor = Color(0xFFE3F2FD),
                unselectedIconColor = Color(0xFF90A4AE),
                unselectedTextColor = Color(0xFF90A4AE),
            ),
    )
}

@Composable
private fun PlaceholderTabScreen(
    title: String,
    body: String,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = OptiFlowNavy)
            Text(text = body, color = OptiFlowMuted)
        }
    }
}
