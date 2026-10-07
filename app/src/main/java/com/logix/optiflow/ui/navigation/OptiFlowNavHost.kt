package com.logix.optiflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.logix.optiflow.ui.auth.AuthScreen
import com.logix.optiflow.ui.booking.BookingScreen
import com.logix.optiflow.ui.search.StoreSearchScreen
import java.util.UUID

object Routes {
    const val SEARCH = "search"
    const val AUTH = "auth"
    const val BOOKING = "booking/{storeId}"

    fun booking(storeId: UUID): String = "booking/$storeId"
}

@Composable
fun OptiFlowNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SEARCH) {
        composable(Routes.SEARCH) {
            StoreSearchScreen(
                onOpenAuth = { navController.navigate(Routes.AUTH) },
                onSelectStore = { storeId ->
                    navController.navigate(Routes.booking(storeId))
                },
            )
        }
        composable(Routes.AUTH) {
            AuthScreen(
                onBack = { navController.popBackStack() },
                onAuthenticated = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.BOOKING,
            arguments = listOf(navArgument("storeId") { type = NavType.StringType }),
        ) { entry ->
            val storeId = UUID.fromString(entry.arguments?.getString("storeId"))
            BookingScreen(
                storeId = storeId,
                onBack = { navController.popBackStack() },
                onNeedAuth = { navController.navigate(Routes.AUTH) },
                onConfirmed = {
                    navController.popBackStack(Routes.SEARCH, inclusive = false)
                },
            )
        }
    }
}
