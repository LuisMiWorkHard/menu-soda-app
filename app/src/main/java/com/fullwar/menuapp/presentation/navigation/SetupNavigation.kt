package com.fullwar.menuapp.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fullwar.menuapp.data.util.AuthEventBus
import com.fullwar.menuapp.presentation.features.home.HomeScreen
import com.fullwar.menuapp.presentation.features.login.LoginScreen
import com.fullwar.menuapp.presentation.features.menu.MenuScreen
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.informacion_personal.InformacionPersonalScreen
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.cambiar_contrasena.CambiarContrasenaScreen
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena.NuevaContrasenaScreen
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena.RecuperarContrasenaScreen
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SetupNavigation(startDestination: String) {
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
        startDestination = startDestination
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(
                onNuevoMenuClick = { dateMillis, conflictoId ->
                    navController.navigate(AppScreens.MenuScreen.withDate(dateMillis, conflictoId))
                },
                onEditarMenuClick = { menuId ->
                    navController.navigate(AppScreens.MenuScreen.withId(menuId))
                },
                onLogout = {
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onVerInformacionPersonalClick = {
                    navController.navigate(AppScreens.InformacionPersonalScreen.route)
                },
                onCambiarContrasenaClick = {
                    navController.navigate(AppScreens.CambiarContrasenaScreen.route)
                }
            )
        }
        composable(route = AppScreens.InformacionPersonalScreen.route) {
            InformacionPersonalScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = AppScreens.CambiarContrasenaScreen.route) {
            CambiarContrasenaScreen(
                onBack      = { navController.popBackStack() },
                onExito     = { navController.popBackStack() },
                onOlvidaste = { navController.navigate(AppScreens.RecuperarContrasenaScreen.route) }
            )
        }
        composable(route = AppScreens.RecuperarContrasenaScreen.route) {
            RecuperarContrasenaScreen(
                onBack           = { navController.popBackStack() },
                onCodigoCorrecto = { navController.navigate(AppScreens.NuevaContrasenaRecuperacionScreen.route) }
            )
        }
        composable(route = AppScreens.NuevaContrasenaRecuperacionScreen.route) {
            NuevaContrasenaScreen(
                onBack  = { navController.popBackStack() },
                onExito = {
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.HomeScreen.route) { inclusive = false }
                    }
                }
            )
        }
        composable(
            route = AppScreens.MenuScreen.route,
            arguments = listOf(
                navArgument("menuId") { type = NavType.IntType; defaultValue = -1 },
                navArgument("selectedDate") { type = NavType.LongType; defaultValue = -1L },
                navArgument("conflictoId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) { backStackEntry ->
            val menuId       = backStackEntry.arguments?.getInt("menuId")?.takeIf { it > 0 }
            val selectedDate = backStackEntry.arguments?.getLong("selectedDate")?.takeIf { it > 0 }
            val conflictoId  = backStackEntry.arguments?.getInt("conflictoId")?.takeIf { it > 0 }
            MenuScreen(
                menuId          = menuId,
                selectedDate    = selectedDate,
                conflictoMenuId = conflictoId,
                onMenuGuardado  = {
                    navController.popBackStack(
                        route = AppScreens.HomeScreen.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}
