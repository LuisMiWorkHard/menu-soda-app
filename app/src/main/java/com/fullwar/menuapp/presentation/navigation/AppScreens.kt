package com.fullwar.menuapp.presentation.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object HomeScreen : AppScreens("home_screen")
    object MenuScreen : AppScreens("menu_screen?menuId={menuId}&selectedDate={selectedDate}&conflictoId={conflictoId}") {
        fun withId(menuId: Int) = "menu_screen?menuId=$menuId"
        fun withDate(dateMillis: Long, conflictoId: Int? = null) =
            "menu_screen?selectedDate=$dateMillis&conflictoId=${conflictoId ?: -1}"
    }
    object InformacionPersonalScreen : AppScreens("informacion_personal_screen")
    object CambiarContrasenaScreen : AppScreens("cambiar_contrasena_screen")
    object RecuperarContrasenaScreen : AppScreens("recuperar_contrasena_screen")
    object NuevaContrasenaRecuperacionScreen : AppScreens("nueva_contrasena_recuperacion_screen")
}
