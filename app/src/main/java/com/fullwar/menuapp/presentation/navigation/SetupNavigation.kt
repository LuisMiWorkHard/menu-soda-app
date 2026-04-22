package com.fullwar.menuapp.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.data.util.AuthEventBus
import com.fullwar.menuapp.presentation.features.home.HomeScreen
import com.fullwar.menuapp.presentation.features.login.LoginScreen
import com.fullwar.menuapp.presentation.features.menu.MenuScreen
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = koinViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AuthEventBus.sessionExpiredEvent.collect {
            Toast.makeText(
                context,
                "Tu sesión ha expirado. Por favor inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            navController.navigate(AppScreens.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(
                onNuevoMenuClick = {
                    navController.navigate(AppScreens.MenuScreen.route)
                }
            )
        }
        composable(route = AppScreens.MenuScreen.route) {
            MenuScreen(
                onMenuGuardado = {
                    navController.popBackStack(
                        route = AppScreens.HomeScreen.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}