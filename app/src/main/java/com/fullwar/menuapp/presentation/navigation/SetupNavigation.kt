package com.fullwar.menuapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.presentation.features.login.LoginScreen

@Composable
fun SetupNavigation(){
    val navController = rememberNavController()
    val sharedViewMOdel: SharedViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.LoginScreen.route
    ){
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, sharedViewModel = sharedViewMOdel)
        }
    }
}