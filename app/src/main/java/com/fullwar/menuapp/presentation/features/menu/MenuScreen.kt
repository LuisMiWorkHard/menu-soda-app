package com.fullwar.menuapp.presentation.features.menu

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.PasoEstiloScreen
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.PasoPlatosFondoScreen
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SeleccionEntradasScreen
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import org.koin.androidx.compose.koinViewModel

sealed class MenuRoute(val route: String) {
    object Entradas : MenuRoute("paso_entradas")
    object PlatosFondo : MenuRoute("paso_platos_fondo")
    object Estilo : MenuRoute("paso_estilo")
}

@Composable
fun MenuScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val menuViewModel: MenuViewModel = koinViewModel()
    val entradaViewModel: EntradaViewModel = koinViewModel()

    val currentStep = when (currentRoute) {
        MenuRoute.Entradas.route -> 1
        MenuRoute.PlatosFondo.route -> 2
        MenuRoute.Estilo.route -> 3
        else -> 1
    }

    val stepTitle = when (currentStep) {
        1 -> stringResource(id = R.string.nuevo_paso1_titulo)
        2 -> stringResource(id = R.string.nuevo_paso2_titulo)
        3 -> stringResource(id = R.string.nuevo_paso3_titulo)
        else -> ""
    }

    val currentSelectedSize = when (currentStep) {
        1 -> menuViewModel.selectedEntradas.size
        2 -> menuViewModel.selectedPlatosFuertes.size
        3 -> menuViewModel.selectedBebidas.size
        else -> 0
    }

    MenuScreenContent(
        currentStep = currentStep,
        stepTitle = stepTitle,
        selectedCount = currentSelectedSize,
        onAnterior = { navController.popBackStack() },
        onSiguiente = {
            when (currentStep) {
                1 -> navController.navigate(MenuRoute.PlatosFondo.route)
                2 -> navController.navigate(MenuRoute.Estilo.route)
                3 -> { /* Finalizar menú */ }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = MenuRoute.Entradas.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(MenuRoute.Entradas.route) {
                SeleccionEntradasScreen(
                    menuViewModel = menuViewModel,
                    entradaViewModel = entradaViewModel
                )
            }
            composable(MenuRoute.PlatosFondo.route) {
                PasoPlatosFondoScreen(menuViewModel = menuViewModel)
            }
            composable(MenuRoute.Estilo.route) {
                PasoEstiloScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    currentStep: Int,
    totalSteps: Int = 3,
    stepTitle: String,
    selectedCount: Int,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear Menú Diario",
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(SpacingLarge),
                    horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = onAnterior,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(CornerRadiusMedium),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text(
                                text = stringResource(id = R.string.nuevo_anterior),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        onClick = onSiguiente,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(CornerRadiusMedium)
                    ) {
                        Text(
                            text = if (currentStep == totalSteps)
                                stringResource(id = R.string.nuevo_finalizar)
                            else
                                stringResource(id = R.string.nuevo_siguiente),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ProgressHeader(
                currentStep = currentStep,
                totalSteps = totalSteps,
                stepTitle = stepTitle,
                selectedCount = selectedCount
            )
            content()
        }
    }
}

@Composable
fun ProgressHeader(currentStep: Int, totalSteps: Int, stepTitle: String, selectedCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingLarge)
            .padding(top = SpacingSmall, bottom = SpacingMedium)
    ) {
        Text(
            text = stringResource(id = R.string.nuevo_progreso_de, currentStep, totalSteps),
            fontSize = TextSizeSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpacingXSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stepTitle,
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.platos_fondo_seleccionados, selectedCount),
                fontSize = TextSizeMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(SpacingXSmall),
            color = MaterialTheme.colorScheme.primary,
            trackColor = HeavyGray,
            strokeCap = StrokeCap.Round
        )
    }
}

// --- Previews: ProgressHeader ---

@Preview(showBackground = true, name = "ProgressHeader - Paso 1 Claro")
@Composable
private fun ProgressHeaderPreview() {
    MenuAppTheme(darkTheme = false) {
        ProgressHeader(currentStep = 1, totalSteps = 3, stepTitle = "Entradas", selectedCount = 2)
    }
}

@Preview(showBackground = true, name = "ProgressHeader - Paso 1 Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ProgressHeaderDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProgressHeader(currentStep = 1, totalSteps = 3, stepTitle = "Entradas", selectedCount = 2)
        }
    }
}

// --- Previews: MenuScreenContent ---

@Preview(showBackground = true, name = "MenuScreen - Paso 1 Claro")
@Composable
private fun MenuScreenStep1Preview() {
    MenuAppTheme(darkTheme = false) {
        MenuScreenContent(
            currentStep = 1,
            stepTitle = "Entradas",
            selectedCount = 2,
            onAnterior = {},
            onSiguiente = {}
        ) {}
    }
}

@Preview(showBackground = true, name = "MenuScreen - Paso 1 Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuScreenStep1DarkPreview() {
    MenuAppTheme(darkTheme = true) {
        MenuScreenContent(
            currentStep = 1,
            stepTitle = "Entradas",
            selectedCount = 2,
            onAnterior = {},
            onSiguiente = {}
        ) {}
    }
}

@Preview(showBackground = true, name = "MenuScreen - Paso 2 Claro")
@Composable
private fun MenuScreenStep2Preview() {
    MenuAppTheme(darkTheme = false) {
        MenuScreenContent(
            currentStep = 2,
            stepTitle = "Platos Fuertes",
            selectedCount = 1,
            onAnterior = {},
            onSiguiente = {}
        ) {}
    }
}

@Preview(showBackground = true, name = "MenuScreen - Paso 2 Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuScreenStep2DarkPreview() {
    MenuAppTheme(darkTheme = true) {
        MenuScreenContent(
            currentStep = 2,
            stepTitle = "Platos Fuertes",
            selectedCount = 1,
            onAnterior = {},
            onSiguiente = {}
        ) {}
    }
}
