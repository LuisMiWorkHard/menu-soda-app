package com.fullwar.menuapp.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
}