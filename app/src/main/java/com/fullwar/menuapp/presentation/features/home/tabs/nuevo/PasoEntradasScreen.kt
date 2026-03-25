package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.entrada.AnadirEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.entrada.EntradaViewModel
import com.fullwar.menuapp.ui.theme.*

data class SugerenciaItem(
    val nombre: String,
    val descripcion: String,
    val icon: ImageVector
)

@Composable
fun PasoEntradasScreen(
    selectedEntradas: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    entradaViewModel: EntradaViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Cargar entradas al inicio
    LaunchedEffect(Unit) {
        entradaViewModel.loadEntradas()
    }

    val entradasState = entradaViewModel.entradasState

    val sugerencias = remember {
        listOf(
            SugerenciaItem(
                "Carpaccio de Betabel",
                "No se ha servido en 15 días. ¡Es hora de volver!",
                Icons.Filled.CalendarMonth
            ),
            SugerenciaItem(
                "Ceviche Clásico",
                "Combina perfectamente con platos de pescado.",
                Icons.Filled.Restaurant
            )
        )
    }

    val todasLasEntradas = when (entradasState) {
        is State.Success -> entradasState.data
        else -> emptyList()
    }

    val entradasSeleccionadas = todasLasEntradas.filter { it.descripcion in selectedEntradas }
    val entradasNoSeleccionadas = todasLasEntradas.filter { it.descripcion !in selectedEntradas }

    // Seleccionados siempre visibles, sin filtrar por búsqueda
    val seleccionadasFiltradas = entradasSeleccionadas

    val noSeleccionadasFiltradas = if (searchQuery.isBlank()) entradasNoSeleccionadas
        else entradasNoSeleccionadas.filter { it.descripcion.contains(searchQuery, ignoreCase = true) }

    val sinResultados = searchQuery.isNotBlank() && noSeleccionadasFiltradas.isEmpty() && entradasSeleccionadas.isEmpty()

    // Bottom sheet para añadir nueva entrada
    if (showBottomSheet) {
        AnadirEntradaBottomSheet(
            viewModel = entradaViewModel,
            onDismiss = {
                entradaViewModel.resetForm()
                showBottomSheet = false
            },
            onSuccess = {
                entradaViewModel.resetForm()
                showBottomSheet = false
            }
        )
    }

    val listState = rememberLazyListState()
    val canScrollDown by remember { derivedStateOf { listState.canScrollForward } }

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {

        // Sugerencias inteligentes - carrusel
        item {
            Spacer(modifier = Modifier.height(SpacingSmall))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = SodaOrange,
                    modifier = Modifier.size(IconSizeMedium)
                )
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(
                    text = stringResource(id = R.string.nuevo_sugerencias),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
            }
            Spacer(modifier = Modifier.height(SpacingSmall))
            SugerenciasCarrusel(sugerencias = sugerencias)
        }

        // Campo de búsqueda
        item {
            Spacer(modifier = Modifier.height(SpacingSmall))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.nuevo_buscar_entradas),
                        color = SodaGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = SodaGray
                    )
                },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SodaOrange,
                    unfocusedBorderColor = SodaGrayLight
                ),
                singleLine = true
            )
        }

        // Listado de entradas - header
        item {
            Spacer(modifier = Modifier.height(SpacingSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.nuevo_listado_entradas),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
                /*
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        entradaViewModel.loadTiposEntrada()
                        showBottomSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = null,
                        tint = SodaOrange,
                        modifier = Modifier.size(IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(SpacingXSmall))
                    Text(
                        text = stringResource(id = R.string.nuevo_anadir_nueva),
                        color = SodaOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = TextSizeSmall
                    )
                }*/
            }
        }

        // Estado de carga
        when (entradasState) {
            is State.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = SpacingXLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SodaOrange)
                    }
                }
            }
            is State.Error -> {
                item {
                    Text(
                        text = entradasState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = TextSizeSmall,
                        modifier = Modifier.padding(vertical = SpacingMedium)
                    )
                }
            }
            is State.Success -> {
                // Seleccionados al tope
                items(seleccionadasFiltradas, key = { it.descripcion }) { entrada ->
                    EntradaListItem(
                        entrada = entrada,
                        isSelected = true,
                        onToggle = { checked ->
                            onSelectionChange(
                                if (checked) selectedEntradas + entrada.descripcion
                                else selectedEntradas - entrada.descripcion
                            )
                        }
                    )
                }

                // Separador entre seleccionados y no seleccionados
                if (seleccionadasFiltradas.isNotEmpty()) {
                    item {
                        HorizontalDivider(thickness = 2.dp, color = SodaOrange.copy(alpha = 0.3f))
                    }
                }

                // No seleccionados
                items(noSeleccionadasFiltradas, key = { it.descripcion }) { entrada ->
                    EntradaListItem(
                        entrada = entrada,
                        isSelected = false,
                        onToggle = { checked ->
                            onSelectionChange(
                                if (checked) selectedEntradas + entrada.descripcion
                                else selectedEntradas - entrada.descripcion
                            )
                        }
                    )
                }

                // Añadir nueva solo si no hay resultados en ninguna de las dos listas
                if (sinResultados) {
                    item {
                        AnadirNuevaListItem(
                            onClick = {
                                entradaViewModel.loadTiposEntrada()
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
            else -> {}
        }

        // Espacio extra para que el botón inferior no tape
        item { Spacer(modifier = Modifier.height(SpacingLarge)) }
    }

    // Indicador de scroll
    AnimatedVisibility(
        visible = canScrollDown,
        modifier = Modifier.align(Alignment.BottomCenter),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
        )
    }
    } // Box
}

@Composable
private fun EntradaListItem(
    entrada: EntradaResponseDto,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val bgColor = if (isSelected) SodaOrangeLight else Color.Transparent
    Column {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(CornerRadiusMedium),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(!isSelected) }
                    .padding(vertical = SpacingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SodaOrange,
                        uncheckedColor = SodaGray
                    )
                )
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(
                    text = entrada.descripcion,
                    fontSize = TextSizeMedium
                )
            }
        }
        HorizontalDivider(color = SodaGrayLight)
    }
}

@Composable
private fun AnadirNuevaListItem(onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                tint = SodaOrange,
                modifier = Modifier.size(IconSizeSmall)
            )
            Spacer(modifier = Modifier.width(SpacingSmall))
            Text(
                text = stringResource(R.string.anadir_nueva),
                color = SodaOrange,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextSizeMedium
            )
        }
        HorizontalDivider(color = SodaGrayLight)
    }
}

@Composable
private fun SugerenciasCarrusel(sugerencias: List<SugerenciaItem>) {
    val pagerState = rememberPagerState(pageCount = { sugerencias.size })

    LaunchedEffect(sugerencias.size) {
        while (true) {
            delay(10000)
            val nextPage = (pagerState.currentPage + 1) % sugerencias.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            SugerenciaCard(sugerencia = sugerencias[page])
        }

        Spacer(modifier = Modifier.height(SpacingSmall))

        // Indicadores de puntos
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(sugerencias.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .background(
                            color = if (isSelected) SodaOrange else SodaGrayLight,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun SugerenciaCard(sugerencia: SugerenciaItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = SodaOrangeLight
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo blanco
            Surface(
                shape = RoundedCornerShape(CornerRadiusSmall),
                color = Color.White,
                modifier = Modifier.size(Spacing4XLarge)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = sugerencia.icon,
                        contentDescription = null,
                        tint = SodaOrange,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.width(SpacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sugerencia.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
                Text(
                    text = sugerencia.descripcion,
                    fontSize = TextSizeSmall,
                    color = SodaGray
                )
            }
        }
    }
}
