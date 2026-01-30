package com.fullwar.menuapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.presentation.features.login.LoginScreen
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupNavigation(){
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ){
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
    }
}