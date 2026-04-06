package com.fullwar.menuapp.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object NuevoMenu : AppScreens("nuevo_menu")
}