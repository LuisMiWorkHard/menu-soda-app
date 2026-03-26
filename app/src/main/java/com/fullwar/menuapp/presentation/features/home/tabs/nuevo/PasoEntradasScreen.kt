package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.fullwar.menuapp.di.Constants
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.entrada.AnadirEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.home.tabs.nuevo.entrada.EntradaViewModel
import com.fullwar.menuapp.ui.theme.*

data class SugerenciaItem(
    val nombre: String,
    val descripcion: String
)

@Composable
fun PasoEntradasScreen(
    selectedEntradas: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    entradaViewModel: EntradaViewModel,
    showSugerencias: Boolean,
    onHideSugerencias: () -> Unit
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
            SugerenciaItem("Carpaccio de Betabel", "No se ha servido en 15 días. ¡Es hora de volver!"),
            SugerenciaItem("Ceviche Clásico", "Combina perfectamente con platos de pescado."),
            SugerenciaItem("Sopa Azteca", "Perfecta para días fríos."),
            SugerenciaItem("Bruschetta de Jitomate", "Opción ligera y fresca."),
            SugerenciaItem("Tabla de Quesos", "Ideal para compartir en la mesa.")
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

    val sinResultados = searchQuery.isNotBlank() && noSeleccionadasFiltradas.isEmpty()

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

        // Sugerencias inteligentes
        if (showSugerencias) {
            item {
                Spacer(modifier = Modifier.height(SpacingSmall))
                Surface(
                    color = SodaOrangeLight,
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(SpacingMedium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = SodaOrange, modifier = Modifier.size(IconSizeSmall))
                                Spacer(modifier = Modifier.width(SpacingSmall))
                                Text(text = stringResource(id = R.string.nuevo_sugerencias), fontWeight = FontWeight.Bold, fontSize = TextSizeSmall)
                            }
                            IconButton(
                                onClick = { onHideSugerencias() },
                                modifier = Modifier.size(IconSizeSmall)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = SodaGray
                                )
                            }
                        }
                        LazyRow(
                            modifier = Modifier.padding(top = SpacingMedium),
                            horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                        ) {
                            items(sugerencias) { sugerencia ->
                                SugerenciaCard(
                                    sugerencia = sugerencia,
                                    onAdd = { onSelectionChange(selectedEntradas + sugerencia.nombre) }
                                )
                            }
                        }
                    }
                }
            }
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
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = if (isSelected) SodaOrangeLight else SodaGrayLight.copy(alpha = 0.3f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = entrada.imagenId?.let { "${Constants.BASE_URL}/api/imagen/$it/contenido" }
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(CornerRadiusSmall)),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(SodaGrayLight),
                    error = ColorPainter(SodaOrangeLight)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(CornerRadiusSmall))
                        .background(SodaGrayLight)
                )
            }
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entrada.descripcion, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = entrada.descripcionLarga, fontSize = TextSizeSmall, color = SodaGray)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = SodaOrange,
                    uncheckedColor = SodaGray
                )
            )
        }
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
fun SugerenciaCard(sugerencia: SugerenciaItem, onAdd: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp),
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sugerencia.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = sugerencia.descripcion, fontSize = TextSizeSmall, color = SodaGray)
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
                    shape = RoundedCornerShape(CornerRadiusSmall),
                    contentPadding = PaddingValues(horizontal = SpacingSmall, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(IconSizeSmall))
                    Spacer(modifier = Modifier.width(SpacingXSmall))
                    Text(text = stringResource(id = R.string.platos_fondo_anadir), fontSize = TextSizeSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(SpacingSmall))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(CornerRadiusSmall))
                    .background(Color.DarkGray)
            )
        }
    }
}
