package com.fullwar.menuapp.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object MenuScreen : AppScreens("menu_screen?menuId={menuId}&selectedDate={selectedDate}&conflictoId={conflictoId}") {
        fun withId(menuId: Int) = "menu_screen?menuId=$menuId"
        fun withDate(dateMillis: Long, conflictoId: Int? = null) =
            "menu_screen?selectedDate=$dateMillis&conflictoId=${conflictoId ?: -1}"
    }
}
