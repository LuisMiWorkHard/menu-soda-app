package com.fullwar.menuapp.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object MenuScreen : AppScreens("menu_screen?menuId={menuId}") {
        fun withId(menuId: Int) = "menu_screen?menuId=$menuId"
    }
}
