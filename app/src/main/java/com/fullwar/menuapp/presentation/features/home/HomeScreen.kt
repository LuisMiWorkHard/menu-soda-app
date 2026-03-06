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
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.features.home.tabs.HistorialTab
import com.fullwar.menuapp.presentation.features.home.tabs.NuevoTab
import com.fullwar.menuapp.presentation.features.home.tabs.PerfilTab
import com.fullwar.menuapp.ui.theme.SodaOrange

enum class HomeTab(val labelRes: Int, val icon: ImageVector) {
    HISTORIAL(R.string.tab_historial, Icons.Filled.History),
    NUEVO(R.string.tab_nuevo, Icons.Filled.Add),
    PERFIL(R.string.tab_perfil, Icons.Filled.Person)
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(HomeTab.HISTORIAL) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
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
        when (selectedTab) {
            HomeTab.HISTORIAL -> HistorialTab(modifier = Modifier.padding(innerPadding))
            HomeTab.NUEVO -> NuevoTab(modifier = Modifier.padding(innerPadding))
            HomeTab.PERFIL -> PerfilTab(modifier = Modifier.padding(innerPadding))
        }
    }
}
