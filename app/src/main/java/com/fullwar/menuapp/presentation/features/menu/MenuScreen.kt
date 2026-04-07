package com.fullwar.menuapp.presentation.features.menu

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.PasoEstiloScreen
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.PasoPlatosFondoScreen
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.PasoEntradasScreen
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import org.koin.androidx.compose.koinViewModel

sealed class NuevoMenuRoute(val route: String) {
    object Entradas : NuevoMenuRoute("paso_entradas")
    object PlatosFondo : NuevoMenuRoute("paso_platos_fondo")
    object Estilo : NuevoMenuRoute("paso_estilo")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMenuScreen(modifier: Modifier = Modifier.Companion) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val menuViewModel: MenuViewModel = koinViewModel()
    val entradaViewModel: EntradaViewModel = koinViewModel()

    // Paso actual derivado de la ruta
    val currentStep = when (currentRoute) {
        NuevoMenuRoute.Entradas.route -> 1
        NuevoMenuRoute.PlatosFondo.route -> 2
        NuevoMenuRoute.Estilo.route -> 3
        else -> 1
    }

    val stepTitle = when (currentStep) {
        1 -> stringResource(id = R.string.nuevo_paso1_titulo)
        2 -> stringResource(id = R.string.nuevo_paso2_titulo)
        3 -> stringResource(id = R.string.nuevo_paso3_titulo)
        else -> ""
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear Menú Diario",
                        fontWeight = FontWeight.Companion.Bold,
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
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(SpacingLarge),
                    horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.Companion.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(CornerRadiusMedium),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Companion.Black)
                        ) {
                            Text(
                                text = stringResource(id = R.string.nuevo_anterior),
                                fontWeight = FontWeight.Companion.Bold
                            )
                        }
                    }
                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> navController.navigate(NuevoMenuRoute.PlatosFondo.route)
                                2 -> navController.navigate(NuevoMenuRoute.Estilo.route)
                                3 -> { /* Finalizar menú */
                                }
                            }
                        },
                        modifier = Modifier.Companion.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                            CornerRadiusMedium
                        )
                    ) {
                        Text(
                            text = if (currentStep == 3) stringResource(id = R.string.nuevo_finalizar) else stringResource(
                                id = R.string.nuevo_siguiente
                            ),
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Barra de progreso
            val currentSelectedSize = when (currentStep) {
                1 -> menuViewModel.selectedEntradas.size
                2 -> menuViewModel.selectedPlatosFuertes.size
                3 -> menuViewModel.selectedBebidas.size
                else -> 0
            }
            ProgressHeader(
                currentStep = currentStep,
                totalSteps = 3,
                stepTitle = stepTitle,
                selectedCount = currentSelectedSize
            )

            // NavHost con los 3 pasos
            NavHost(
                navController = navController,
                startDestination = NuevoMenuRoute.Entradas.route,
                modifier = Modifier.Companion.fillMaxSize()
            ) {
                composable(NuevoMenuRoute.Entradas.route) {
                    PasoEntradasScreen(
                        menuViewModel = menuViewModel,
                        entradaViewModel = entradaViewModel
                    )
                }
                composable(NuevoMenuRoute.PlatosFondo.route) {
                    PasoPlatosFondoScreen(menuViewModel = menuViewModel)
                }
                composable(NuevoMenuRoute.Estilo.route) {
                    PasoEstiloScreen()
                }
            }
        }
    }
}

@Composable
fun ProgressHeader(currentStep: Int, totalSteps: Int, stepTitle: String, selectedCount: Int) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = SpacingLarge)
            .padding(top = SpacingSmall, bottom = SpacingMedium)
    ) {
        Text(
            text = stringResource(id = R.string.nuevo_progreso_de, currentStep, totalSteps),
            fontSize = TextSizeSmall,
            fontWeight = FontWeight.Companion.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(top = SpacingXSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.Bottom
        ) {
            Text(
                text = stepTitle.replace("Paso $currentStep: ", ""),
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Companion.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.platos_fondo_seleccionados, selectedCount),
                fontSize = TextSizeMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.Companion.height(SpacingSmall))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(SpacingXSmall),
            color = MaterialTheme.colorScheme.primary,
            trackColor = HeavyGray,
            strokeCap = StrokeCap.Companion.Round
        )
    }
}