package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                        text = stepTitle,
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
            val nextButtonText = when (currentStep) {
                1 -> stringResource(id = R.string.nuevo_siguiente_platos)
                2 -> stringResource(id = R.string.nuevo_siguiente_bebidas)
                3 -> stringResource(id = R.string.nuevo_finalizar)
                else -> ""
            }

            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        when (currentStep) {
                            1 -> navController.navigate(NuevoMenuRoute.PlatosFondo.route)
                            2 -> navController.navigate(NuevoMenuRoute.Estilo.route)
                            3 -> { /* Finalizar menú */ }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpacingLarge),
                    colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadiusMedium)
                ) {
                    Text(
                        text = nextButtonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeMedium
                    )
                    Spacer(modifier = Modifier.width(SpacingSmall))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
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
            ProgressHeader(currentStep = currentStep, totalSteps = 3)

            // NavHost con los 3 pasos
            NavHost(
                navController = navController,
                startDestination = NuevoMenuRoute.Entradas.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(NuevoMenuRoute.Entradas.route) {
                    PasoEntradasScreen(
                        selectedEntradas = selectedEntradas,
                        onSelectionChange = { selectedEntradas = it }
                    )
                }
                composable(NuevoMenuRoute.PlatosFondo.route) {
                    PasoPlatosFondoScreen()
                }
                composable(NuevoMenuRoute.Estilo.route) {
                    PasoEstiloScreen()
                }
            }
        }
    }
}

@Composable
fun ProgressHeader(currentStep: Int, totalSteps: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingLarge)
            .padding(top = SpacingSmall, bottom = SpacingMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.nuevo_progreso_label),
                fontSize = TextSizeSmall,
                color = SodaGray
            )
            Text(
                text = stringResource(id = R.string.nuevo_progreso_de, currentStep, totalSteps),
                fontSize = TextSizeSmall,
                fontWeight = FontWeight.Bold,
                color = SodaGray
            )
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(SpacingSmall),
            color = SodaOrange,
            trackColor = SodaGrayLight,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
