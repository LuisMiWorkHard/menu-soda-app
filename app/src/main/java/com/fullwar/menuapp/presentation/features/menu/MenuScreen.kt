package com.fullwar.menuapp.presentation.features.menu

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.components.ItemListSkeleton
import com.fullwar.menuapp.presentation.common.utils.State
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
import com.fullwar.menuapp.presentation.features.menu.estilo.SaveUiState
import com.fullwar.menuapp.presentation.features.menu.estilo.SeleccionEstiloViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.seleccion.SeleccionPlatosFondoScreen
import com.fullwar.menuapp.presentation.features.menu.plato.seleccion.SeleccionPlatosFondoViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SelectedEntradasBottomSheetContent
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SeleccionEntradasScreen
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SeleccionEntradasViewModel
import com.fullwar.menuapp.presentation.features.menu.estilo.SeleccionEstiloScreen
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.seleccion.SelectedPlatosFondoBottomSheetContent
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
fun MenuScreen(menuId: Int? = null, onMenuGuardado: () -> Unit = {}) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val menuViewModel: MenuViewModel = koinViewModel()

    val entradaViewModel: EntradaViewModel = koinViewModel()
    val seleccionEntradasViewModel: SeleccionEntradasViewModel = koinViewModel()
    val platoViewModel: PlatoViewModel = koinViewModel()
    val seleccionPlatosFondoViewModel: SeleccionPlatosFondoViewModel = koinViewModel()
    val pasoEstiloViewModel: SeleccionEstiloViewModel = koinViewModel()

    LaunchedEffect(menuId) {
        menuId?.let {
            menuViewModel.initEditMode(it)
            pasoEstiloViewModel.clearSelectedImagen()
        }
    }

    // Pre-selección centralizada para modo edición
    LaunchedEffect(seleccionEntradasViewModel.entradasState, menuViewModel.preSelectedEntradasIds) {
        val state = seleccionEntradasViewModel.entradasState
        if (state !is State.Success) return@LaunchedEffect
        val ids = menuViewModel.preSelectedEntradasIds
        if (ids.isEmpty() || menuViewModel.selectedEntradas.isNotEmpty()) return@LaunchedEffect
        state.data.filter { it.id in ids }.takeIf { it.isNotEmpty() }
            ?.let { menuViewModel.updateEntradas(it) }
    }

    LaunchedEffect(seleccionPlatosFondoViewModel.platosState, menuViewModel.preSelectedPlatosIds) {
        val state = seleccionPlatosFondoViewModel.platosState
        if (state !is State.Success) return@LaunchedEffect
        val ids = menuViewModel.preSelectedPlatosIds
        if (ids.isEmpty() || menuViewModel.selectedPlatosFuertes.isNotEmpty()) return@LaunchedEffect
        state.data.filter { it.id in ids }.takeIf { it.isNotEmpty() }
            ?.let { menuViewModel.updatePlatosFuertes(it) }
    }

    LaunchedEffect(pasoEstiloViewModel.imagenesState, menuViewModel.preSelectedImagenId) {
        val state = pasoEstiloViewModel.imagenesState
        if (state !is State.Success) return@LaunchedEffect
        val preId = menuViewModel.preSelectedImagenId ?: return@LaunchedEffect
        if (pasoEstiloViewModel.selectedImagenId != null) return@LaunchedEffect
        if (state.data.any { it.id == preId }) pasoEstiloViewModel.selectImagen(preId)
    }

    var isExpanded by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

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

    LaunchedEffect(
        currentStep,
        seleccionEntradasViewModel.entradasState,
        seleccionPlatosFondoViewModel.platosState,
        pasoEstiloViewModel.imagenesState
    ) {
        if (!isRefreshing) return@LaunchedEffect
        val done = when (currentStep) {
            1 -> seleccionEntradasViewModel.entradasState.let { it !is State.Loading && it !is State.Initial }
            2 -> seleccionPlatosFondoViewModel.platosState.let { it !is State.Loading && it !is State.Initial }
            3 -> pasoEstiloViewModel.imagenesState.let { it !is State.Loading && it !is State.Initial }
            else -> true
        }
        if (done) isRefreshing = false
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        when (currentStep) {
            1 -> seleccionEntradasViewModel.loadEntradas()
            2 -> seleccionPlatosFondoViewModel.loadPlatos()
            3 -> pasoEstiloViewModel.loadImagenes()
        }
    }

    val isSiguienteEnabled = when (currentStep) {
        3 -> pasoEstiloViewModel.selectedImagenId != null &&
                pasoEstiloViewModel.saveState !is SaveUiState.Loading
        else -> true
    }

    MenuScreenContent(
        currentStep = currentStep,
        screenTitle = if (menuViewModel.isEditMode) "Editar Menú Diario" else "Crear Menú Diario",
        stepTitle = stepTitle,
        selectedCount = currentSelectedSize,
        isSiguienteEnabled = isSiguienteEnabled,
        isExpanded = isExpanded,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onExpandedChange = { isExpanded = it },
        onAnterior = { navController.popBackStack() },
        onSiguiente = {
            when (currentStep) {
                1 -> navController.navigate(MenuRoute.PlatosFondo.route)
                2 -> navController.navigate(MenuRoute.Estilo.route)
                3 -> pasoEstiloViewModel.onFinalizarClicked()
            }
        },
        bottomSheetContent = {
            when (currentStep) {
                1 -> {
                    SelectedEntradasBottomSheetContent(
                        entradas = menuViewModel.selectedEntradas,
                        imagenesMap = seleccionEntradasViewModel.imagenesMap,
                        onRemove = { entrada ->
                            menuViewModel.updateEntradas(menuViewModel.selectedEntradas - entrada)
                        },
                        onMove = { from, to -> menuViewModel.moveEntrada(from, to) }
                    )
                }
                2 -> {
                    SelectedPlatosFondoBottomSheetContent(
                        platos = menuViewModel.selectedPlatosFuertes,
                        imagenesMap = seleccionPlatosFondoViewModel.imagenesMap,
                        onRemove = { plato ->
                            menuViewModel.updatePlatosFuertes(menuViewModel.selectedPlatosFuertes - plato)
                        },
                        onMove = { from, to -> menuViewModel.movePlato(from, to) }
                    )
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = MenuRoute.Entradas.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(MenuRoute.Entradas.route) {
                val detailError = menuViewModel.menuDetailError
                when {
                    menuViewModel.isEditMode && menuViewModel.isLoadingMenuDetail ->
                        MenuDetailLoadingSkeleton()
                    menuViewModel.isEditMode && detailError != null ->
                        ErrorBanner(
                            message = detailError,
                            modifier = Modifier.padding(SpacingLarge),
                            onRetry = menuViewModel::retryLoadMenuDetail
                        )
                    else -> SeleccionEntradasScreen(
                        menuViewModel = menuViewModel,
                        entradaViewModel = entradaViewModel,
                        seleccionViewModel = seleccionEntradasViewModel
                    )
                }
            }
            composable(MenuRoute.PlatosFondo.route) {
                val detailError = menuViewModel.menuDetailError
                when {
                    menuViewModel.isEditMode && menuViewModel.isLoadingMenuDetail ->
                        MenuDetailLoadingSkeleton()
                    menuViewModel.isEditMode && detailError != null ->
                        ErrorBanner(
                            message = detailError,
                            modifier = Modifier.padding(SpacingLarge),
                            onRetry = menuViewModel::retryLoadMenuDetail
                        )
                    else -> SeleccionPlatosFondoScreen(
                        menuViewModel = menuViewModel,
                        platoViewModel = platoViewModel,
                        seleccionViewModel = seleccionPlatosFondoViewModel
                    )
                }
            }
            composable(MenuRoute.Estilo.route) {
                val detailError = menuViewModel.menuDetailError
                when {
                    menuViewModel.isEditMode && menuViewModel.isLoadingMenuDetail ->
                        MenuDetailLoadingSkeleton()
                    menuViewModel.isEditMode && detailError != null ->
                        ErrorBanner(
                            message = detailError,
                            modifier = Modifier.padding(SpacingLarge),
                            onRetry = menuViewModel::retryLoadMenuDetail
                        )
                    else -> SeleccionEstiloScreen(
                        menuViewModel = menuViewModel,
                        pasoEstiloViewModel = pasoEstiloViewModel,
                        onMenuGuardado = onMenuGuardado
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreenContent(
    currentStep: Int,
    totalSteps: Int = 3,
    screenTitle: String = "Crear Menú Diario",
    stepTitle: String,
    selectedCount: Int,
    isSiguienteEnabled: Boolean = true,
    isExpanded: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {},
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    bottomSheetContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
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
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = CornerRadiusMedium, topEnd = CornerRadiusMedium)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header del BottomSheet (solo pasos 1 y 2 con elementos seleccionados)
                    if ((currentStep == 1 || currentStep == 2) && selectedCount > 0) {
                        // Drag handle visual
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = SpacingSmall),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(HeavyGray, RoundedCornerShape(2.dp))
                            )
                        }

                        // Etiqueta Seleccionados e Icono
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onExpandedChange(!isExpanded) }
                                .padding(horizontal = SpacingLarge, vertical = SpacingMedium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.platos_fondo_seleccionados, selectedCount),
                                fontWeight = FontWeight.Bold,
                                fontSize = TextSizeMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            bottomSheetContent()
                        }
                    }

                    // Botones de navegación (siempre visibles en el bottom bar)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = SpacingLarge, vertical = SpacingMedium),
                        horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                    ) {
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = onAnterior,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
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
                            enabled = isSiguienteEnabled,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
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
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
            if(currentStep != 3) {
                Text(
                    text = stringResource(id = R.string.platos_fondo_seleccionados, selectedCount),
                    fontSize = TextSizeMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
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

@Composable
private fun MenuDetailLoadingSkeleton() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        item { Spacer(modifier = Modifier.height(SpacingMedium)) }
        repeat(6) { item { ItemListSkeleton() } }
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

@Preview(showBackground = true, name = "MenuScreen - Selección Expandida Claro")
@Composable
private fun MenuScreenSelectionPreview() {
    val mockEntradas = listOf(
        com.fullwar.menuapp.data.model.EntradaResponseDto(
            id = 1,
            nombre = "Sopa de Mote",
            estadoId = 1,
            tipoEntradaId = 1,
            fechaRegistro = "",
            usuarioRegistro = ""
        ),
        com.fullwar.menuapp.data.model.EntradaResponseDto(
            id = 2,
            nombre = "Ensalada Rusa",
            estadoId = 1,
            tipoEntradaId = 1,
            fechaRegistro = "",
            usuarioRegistro = ""
        )
    )

    MenuAppTheme(darkTheme = false) {
        MenuScreenContent(
            currentStep = 1,
            stepTitle = "Entradas",
            selectedCount = mockEntradas.size,
            isExpanded = true,
            onAnterior = {},
            onSiguiente = {},
            bottomSheetContent = {
                SelectedEntradasBottomSheetContent(
                    entradas = mockEntradas,
                    imagenesMap = emptyMap(),
                    onRemove = {},
                    onMove = { _, _ -> }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Contenido de la pantalla")
            }
        }
    }
}

@Preview(showBackground = true, name = "MenuScreen - Selección Expandida Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuScreenSelectionDarkPreview() {
    val mockEntradas = listOf(
        com.fullwar.menuapp.data.model.EntradaResponseDto(
            id = 1,
            nombre = "Sopa de Mote",
            descripcion = "Una sopita bien rica",
            estadoId = 1,
            tipoEntradaId = 1,
            fechaRegistro = "",
            usuarioRegistro = ""
        ),
        com.fullwar.menuapp.data.model.EntradaResponseDto(
            id = 2,
            nombre = "Ensalada Rusa",
            descripcion = "Ensalda en base de veterraga",
            estadoId = 1,
            tipoEntradaId = 1,
            fechaRegistro = "",
            usuarioRegistro = ""
        )
    )

    MenuAppTheme(darkTheme = true) {
        MenuScreenContent(
            currentStep = 1,
            stepTitle = "Entradas",
            selectedCount = mockEntradas.size,
            isExpanded = true,
            onAnterior = {},
            onSiguiente = {},
            bottomSheetContent = {
                SelectedEntradasBottomSheetContent(
                    entradas = mockEntradas,
                    imagenesMap = emptyMap(),
                    onRemove = {},
                    onMove = { _, _ -> }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Contenido de la pantalla")
            }
        }
    }
}