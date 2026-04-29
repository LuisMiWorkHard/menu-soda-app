package com.fullwar.menuapp.presentation.features.menu.plato.seleccion

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.*
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.nuevo.NuevoPlatoBottomSheet
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.ui.theme.*
import kotlinx.coroutines.delay

data class SugerenciaPlatoItem(
    val nombre: String,
    val descripcion: String
)

@Composable
fun SeleccionPlatosFondoScreen(
    menuViewModel: MenuViewModel,
    platoViewModel: PlatoViewModel,
    seleccionViewModel: SeleccionPlatosFondoViewModel
) {
    val selectedPlatos = menuViewModel.selectedPlatosFuertes
    val showSugerencias = menuViewModel.showSugerencias
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var pendingAutoSelectNombre by remember { mutableStateOf<String?>(null) }

    // Cargar platos al inicio
    LaunchedEffect(Unit) {
        seleccionViewModel.loadPlatos()
    }

    // Búsqueda fuzzy con debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            seleccionViewModel.resetSearch()
        } else {
            delay(300)
            seleccionViewModel.searchPlatos(searchQuery)
        }
    }

    val platosState = seleccionViewModel.platosState

    // Auto-seleccionar elemento recién creado cuando la lista se refresca
    LaunchedEffect(platosState) {
        val nombre = pendingAutoSelectNombre ?: return@LaunchedEffect
        if (platosState is State.Success) {
            val dto = platosState.data.find { it.nombre == nombre }
            dto?.let {
                menuViewModel.updatePlatosFuertes(menuViewModel.selectedPlatosFuertes + it)
                pendingAutoSelectNombre = null
            }
        }
    }

    val sugerenciasMock = listOf(
        SugerenciaPlatoItem("Lomo Saltado", "Ideal con la Sopa del día."),
        SugerenciaPlatoItem("Ceviche", "Opción fresca para hoy."),
        SugerenciaPlatoItem("Ají de Gallina", "Clásico y contundente."),
        SugerenciaPlatoItem("Tallarines Verdes", "Acompañado de apanado."),
        SugerenciaPlatoItem("Arroz con Mariscos", "Sabor a mar del día.")
    )

    val sugerencias = remember(selectedPlatos) {
        val sugerenciasVisibles = sugerenciasMock.filter { sug -> selectedPlatos.none { it.nombre == sug.nombre } }
        val startIndex = (selectedPlatos.size * 2) % (if (sugerenciasVisibles.isEmpty()) 1 else sugerenciasVisibles.size)
        val end = minOf(startIndex + 3, sugerenciasVisibles.size)
        val selected = if (sugerenciasVisibles.isEmpty()) emptyList() else sugerenciasVisibles.subList(startIndex, end)
        if (selected.size < 3 && sugerenciasVisibles.size >= 3) (selected + sugerenciasVisibles.take(3 - selected.size)) else selected
    }

    val todosLosPlatos = when (platosState) {
        is State.Success -> platosState.data
        else -> emptyList()
    }

    val searchResults = seleccionViewModel.searchResults
    
    // Filtrar para mostrar solo los NO seleccionados en la lista principal
    val noSeleccionadosFiltrados = searchResults.filter { p -> selectedPlatos.none { s -> s.id == p.id } }

    val sinResultados = searchQuery.isNotBlank() && noSeleccionadosFiltrados.isEmpty()

    val listState = rememberLazyListState()
    val canScrollDown by remember { derivedStateOf { listState.canScrollForward } }

    // Bottom sheet para añadir nuevo plato
    if (showBottomSheet) {
        NuevoPlatoBottomSheet(
            viewModel = platoViewModel,
            onDismiss = {
                platoViewModel.initForCreate()
                showBottomSheet = false
            },
            onSuccess = {
                val nombre = (platoViewModel.formFields.fields["platnom"] as? TextFieldValue)?.text?.trim() ?: ""
                if (nombre.isNotEmpty()) pendingAutoSelectNombre = nombre
                platoViewModel.initForCreate()
                seleccionViewModel.loadPlatos()
                showBottomSheet = false
                searchQuery = ""
            },
            onSelectExisting = { dto ->
                menuViewModel.updatePlatosFuertes(menuViewModel.selectedPlatosFuertes + dto)
                platoViewModel.initForCreate()
                showBottomSheet = false
                searchQuery = ""
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SpacingLarge),
            verticalArrangement = Arrangement.spacedBy(SpacingMedium)
        ) {
            // Sugerencias Inteligentes — solo si hay platos cargados y hay sugerencias visibles
            if (showSugerencias && todosLosPlatos.isNotEmpty() && sugerencias.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
                                    Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = YellowIdea, modifier = Modifier.size(IconSizeSmall))
                                    Spacer(modifier = Modifier.width(SpacingSmall))
                                    Text(text = stringResource(id = R.string.platos_fondo_sugerencias), fontWeight = FontWeight.Bold, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(
                                    onClick = { menuViewModel.hideSugerencias() },
                                    modifier = Modifier.size(IconSizeSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            LazyRow(
                                modifier = Modifier.padding(top = SpacingMedium),
                                horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                            ) {
                                items(sugerencias) { item ->
                                    SugerenciaPlatoCard(item) {
                                        val dto = todosLosPlatos.find { it.nombre == item.nombre }
                                        dto?.let { menuViewModel.updatePlatosFuertes(menuViewModel.selectedPlatosFuertes + it) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Buscador
            item {
                Spacer(modifier = Modifier.height(SpacingSmall))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = RoundedCornerShape(CornerRadiusMedium)
                        ),
                    placeholder = { Text(text = stringResource(id = R.string.platos_fondo_buscar), color = HeavyGray) },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = HeavyGray) },
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )
            }

            // Header listado
            item {
                Spacer(modifier = Modifier.height(SpacingSmall))
                Text(
                    text = stringResource(id = R.string.platos_fondo_disponibles),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
            }

            // Estado de carga
            when (platosState) {
                is State.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = SpacingXLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                is State.Error -> {
                    item {
                        ErrorBanner(
                            message = platosState.message,
                            modifier = Modifier.padding(vertical = SpacingSmall)
                        )
                    }
                }
                is State.Success -> {
                    // Botón añadir nueva cuando la búsqueda no tiene resultados
                    if (sinResultados) {
                        item {
                            AnadirNuevoListItem(
                                onClick = {
                                    platoViewModel.loadTiposPlato()
                                    platoViewModel.initForCreate()
                                    platoViewModel.updateField("platnom", TextFieldValue(searchQuery))
                                    showBottomSheet = true
                                }
                            )
                        }
                    }

                    // Sólo mostramos los NO seleccionados (los seleccionados están en el BottomSheet)
                    items(noSeleccionadosFiltrados, key = { it.id }) { plato ->
                        PlatoDisponibleCard(
                            plato = plato,
                            imageUrl = seleccionViewModel.imagenesMap[plato.imagenId],
                            isSelected = false,
                            onToggle = { checked ->
                                if (checked) {
                                    menuViewModel.updatePlatosFuertes(selectedPlatos + plato)
                                }
                            }
                        )
                    }
                }
                else -> {}
            }

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
fun SelectedPlatosFondoBottomSheetContent(
    platos: List<PlatoResponseDto>,
    imagenesMap: Map<Int, String>,
    onRemove: (PlatoResponseDto) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    // Estado para seguir el desplazamiento del ítem arrastrado
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffset by remember { mutableFloatStateOf(0f) }
    val listSize by rememberUpdatedState(platos.size)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp)
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        itemsIndexed(platos, key = { _, plato -> plato.id }) { index, plato ->
            // Usar rememberUpdatedState para asegurar que las lambdas capturen el índice actual
            val currentIndexState by rememberUpdatedState(index)
            val isDragging = draggedItemIndex == currentIndexState
            
            Surface(
                shape = RoundedCornerShape(CornerRadiusMedium),
                color = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isDragging) 8.dp else 0.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = if (isDragging) draggingOffset else 0f
                        scaleX = if (isDragging) 1.02f else 1.0f
                        scaleY = if (isDragging) 1.02f else 1.0f
                    }
            ) {
                Row(
                    modifier = Modifier.padding(SpacingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomImageView(
                        imageUrl = imagenesMap[plato.imagenId],
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(SpacingMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plato.nombre.toSmartUpperCase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = TextSizeXSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = plato.descripcion.toSmartUpperCase(),
                            fontSize = TextSizeXXSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Manija de Arrastre (Drag Handle)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .pointerInput(plato.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { draggedItemIndex = currentIndexState },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        draggingOffset += dragAmount.y
                                        
                                        val activeIndex = draggedItemIndex ?: currentIndexState
                                        val threshold = 50f
                                        
                                        val targetIndex = when {
                                            draggingOffset > threshold && activeIndex < listSize - 1 -> activeIndex + 1
                                            draggingOffset < -threshold && activeIndex > 0 -> activeIndex - 1
                                            else -> null
                                        }
                                        
                                        if (targetIndex != null) {
                                            onMove(activeIndex, targetIndex)
                                            draggedItemIndex = targetIndex
                                            draggingOffset += if (targetIndex > activeIndex) -threshold else threshold
                                        }
                                    },
                                    onDragEnd = {
                                        draggedItemIndex = null
                                        draggingOffset = 0f
                                    },
                                    onDragCancel = {
                                        draggedItemIndex = null
                                        draggingOffset = 0f
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DragHandle,
                            contentDescription = "Arrastrar",
                            tint = if (isDragging) MaterialTheme.colorScheme.primary else HeavyGray
                        )
                    }

                    IconButton(onClick = { onRemove(plato) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remover",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(SpacingSmall)) }
    }
}

@Composable
fun SugerenciaPlatoCard(item: SugerenciaPlatoItem, onAdd: () -> Unit) {
    val shape = RoundedCornerShape(CornerRadiusSmall)

    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .width(280.dp)
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = item.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(CornerRadiusSmall),
                    contentPadding = PaddingValues(horizontal = SpacingSmall, vertical = 0.dp),
                    modifier = Modifier.heightIn(min = 32.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(IconSizeSmall))
                    Spacer(modifier = Modifier.width(SpacingXSmall))
                    Text(text = stringResource(id = R.string.platos_fondo_anadir), fontSize = TextSizeSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
            Spacer(modifier = Modifier.width(SpacingSmall))
            Image(
                painter = painterResource(id = R.drawable.default_image_meal),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ImageSizeXLarge)
                    .clip(shape)
                    .background(HeavyGray, shape)
            )
        }
    }
}

@Composable
fun PlatoDisponibleCard(plato: PlatoResponseDto, imageUrl: String?, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomImageView(imageUrl = imageUrl, sizeDp = 60, defaultImageRes = R.drawable.default_image_menu)
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plato.nombre.toSmartUpperCase(), fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = plato.descripcion.toSmartUpperCase(), fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = HeavyGray
                )
            )
        }
    }
}

@Composable
private fun AnadirNuevoListItem(onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(IconSizeSmall)
            )
            Spacer(modifier = Modifier.width(SpacingSmall))
            Text(
                text = stringResource(R.string.anadir_nuevo),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextSizeMedium
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
    }
}

// --- Previews ---

private class FakeMenuImagenRepository : IMenuImagenRepository {
    override suspend fun getMenuImagenes(): List<MenuImagenResponseDto> = emptyList()
}

private class FakePlatoRepository : IPlatoRepository {
    private val all = listOf(
        PlatoResponseDto(id = 1, nombre = "Pollo a la Plancha", descripcion = "Con ensalada fresca o papas", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        PlatoResponseDto(id = 2, nombre = "Salmón al Horno", descripcion = "Acompañado de espárragos", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        PlatoResponseDto(id = 3, nombre = "Seco de Res", descripcion = "Clásico con frijoles y arroz", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
    )
    override suspend fun getPlatos() = all
    override suspend fun searchPlatos(query: String) =
        if (query.isBlank()) all else all.filter { it.nombre.contains(query, ignoreCase = true) }
    override suspend fun findSimilarPlatos(nombre: String, excludeId: Int?) = emptyList<PlatoResponseDto>()
    override suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto = throw NotImplementedError()
    override suspend fun updatePlato(id: Int, request: PlatoUpdateRequestDto) = throw NotImplementedError()
    override suspend fun getTiposPlato(): List<TipoPlatoResponseDto> = listOf(
        TipoPlatoResponseDto(1, "Carnes", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(2, "Pescados", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(3, "Vegetariano", 1, "01/01/2024", "admin")
    )
    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String, extension: String): ImagenResponseDto = throw NotImplementedError()
}

private val fakePlatoResponseDto = PlatoResponseDto(
    id = 1, nombre = "Pollo a la Plancha", descripcion = "Con ensalada fresca o papas",
    tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"
)

private val fakeSugerenciaPlato = SugerenciaPlatoItem(
    nombre = "Lomo Saltado",
    descripcion = "Ideal con la Sopa del día."
)

@Preview(showBackground = true, name = "PasoPlatosFondo - Claro")
@Composable
private fun SeleccionPlatosFondoScreenPreview() {
    val menuVm = remember { MenuViewModel() }
    val platoVm = remember { PlatoViewModel(FakePlatoRepository()) }
    val seleccionVm = remember { SeleccionPlatosFondoViewModel(FakePlatoRepository(), FakeMenuImagenRepository()) }
    MenuAppTheme(darkTheme = false) {
        SeleccionPlatosFondoScreen(menuViewModel = menuVm, platoViewModel = platoVm, seleccionViewModel = seleccionVm)
    }
}

@Preview(showBackground = true, name = "PasoPlatosFondo - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionPlatosFondoScreenDarkPreview() {
    val menuVm = remember { MenuViewModel() }
    val platoVm = remember { PlatoViewModel(FakePlatoRepository()) }
    val seleccionVm = remember { SeleccionPlatosFondoViewModel(FakePlatoRepository(), FakeMenuImagenRepository()) }
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionPlatosFondoScreen(menuViewModel = menuVm, platoViewModel = platoVm, seleccionViewModel = seleccionVm)
        }
    }
}

@Preview(showBackground = true, name = "SugerenciaPlatoCard - Claro")
@Composable
private fun SugerenciaPlatoCardPreview() {
    MenuAppTheme(darkTheme = false) { SugerenciaPlatoCard(item = fakeSugerenciaPlato, onAdd = {}) }
}

@Preview(showBackground = true, name = "SugerenciaPlatoCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SugerenciaPlatoCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SugerenciaPlatoCard(item = fakeSugerenciaPlato, onAdd = {})
        }
    }
}

@Preview(showBackground = true, name = "PlatoDisponibleCard - Seleccionado")
@Composable
private fun PlatoDisponibleCardSelectedPreview() {
    MenuAppTheme(darkTheme = false) {
        PlatoDisponibleCard(plato = fakePlatoResponseDto, imageUrl = null, isSelected = true, onToggle = {})
    }
}

@Preview(showBackground = true, name = "PlatoDisponibleCard - No seleccionado")
@Composable
private fun PlatoDisponibleCardUnselectedPreview() {
    MenuAppTheme(darkTheme = false) {
        PlatoDisponibleCard(plato = fakePlatoResponseDto, imageUrl = null, isSelected = false, onToggle = {})
    }
}

@Preview(showBackground = true, name = "PlatoDisponibleCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoDisponibleCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlatoDisponibleCard(plato = fakePlatoResponseDto, imageUrl = null, isSelected = false, onToggle = {})
        }
    }
}
