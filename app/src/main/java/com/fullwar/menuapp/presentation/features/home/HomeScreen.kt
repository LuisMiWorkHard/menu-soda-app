package com.fullwar.menuapp.presentation.features.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.features.home.tabs.HistorialTab
import com.fullwar.menuapp.presentation.features.home.tabs.NuevoTab
import com.fullwar.menuapp.presentation.features.home.tabs.PerfilTab
import com.fullwar.menuapp.ui.theme.SodaOrange

enum class HomeTab(val route: String, val labelRes: Int, val icon: ImageVector) {
    HISTORIAL("historial", R.string.tab_historial, Icons.Filled.History),
    NUEVO("nuevo", R.string.tab_nuevo, Icons.Filled.Add),
    PERFIL("perfil", R.string.tab_perfil, Icons.Filled.Person)
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState()
        .value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        label = { Text(text = stringResource(id = tab.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SodaOrange,
                            selectedTextColor = SodaOrange,
                            indicatorColor = Color.White
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeTab.HISTORIAL.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeTab.HISTORIAL.route) { HistorialTab() }
            composable(HomeTab.NUEVO.route) { NuevoTab() }
            composable(HomeTab.PERFIL.route) { PerfilTab() }
        }
    }
}
