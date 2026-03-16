package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.*
import org.koin.androidx.compose.koinViewModel

sealed class NuevoMenuRoute(val route: String) {
    object Entradas : NuevoMenuRoute("paso_entradas")
    object PlatosFondo : NuevoMenuRoute("paso_platos_fondo")
    object Estilo : NuevoMenuRoute("paso_estilo")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMenuScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val entradaViewModel: EntradaViewModel = koinViewModel()

    // Estado compartido entre pasos
    var selectedEntradas by remember { mutableStateOf(setOf<String>()) }
    var selectedPlatosFuertes by remember { mutableStateOf(setOf<String>()) }
    var selectedBebidas by remember { mutableStateOf(setOf<String>()) }

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
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!navController.popBackStack()) {
                                // Estamos en el paso 1, no hacer nada o se podría navegar fuera
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpacingLarge),
                    horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadiusMedium),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                        ) {
                            Text(
                                text = stringResource(id = R.string.nuevo_anterior),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> navController.navigate(NuevoMenuRoute.PlatosFondo.route)
                                2 -> navController.navigate(NuevoMenuRoute.Estilo.route)
                                3 -> { /* Finalizar menú */ }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadiusMedium)
                    ) {
                        Text(
                            text = if (currentStep == 3) stringResource(id = R.string.nuevo_finalizar) else stringResource(id = R.string.nuevo_siguiente),
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
                .background(Color.White)
        ) {
            // Barra de progreso
            val currentSelectedSize = when (currentStep) {
                1 -> selectedEntradas.size
                2 -> selectedPlatosFuertes.size
                3 -> selectedBebidas.size
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
                modifier = Modifier.fillMaxSize()
            ) {
                composable(NuevoMenuRoute.Entradas.route) {
                    PasoEntradasScreen(
                        selectedEntradas = selectedEntradas,
                        onSelectionChange = { selectedEntradas = it },
                        entradaViewModel = entradaViewModel
                    )
                }
                composable(NuevoMenuRoute.PlatosFondo.route) {
                    PasoPlatosFondoScreen(
                        selectedPlatos = selectedPlatosFuertes,
                        onSelectionChange = { selectedPlatosFuertes = it }
                    )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingLarge)
            .padding(top = SpacingSmall, bottom = SpacingMedium)
    ) {
        Text(
            text = stringResource(id = R.string.nuevo_progreso_de, currentStep, totalSteps),
            fontSize = TextSizeSmall,
            fontWeight = FontWeight.Bold,
            color = SodaOrange
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpacingXSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stepTitle.replace("Paso $currentStep: ", ""),
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = stringResource(id = R.string.platos_fondo_seleccionados, selectedCount),
                fontSize = TextSizeMedium,
                color = SodaGray
            )
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(SpacingXSmall),
            color = SodaOrange,
            trackColor = SodaGrayLight,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
