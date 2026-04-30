package com.fullwar.menuapp.presentation.features.menu.entrada.seleccion

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
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.nuevo.NuevaEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.ui.theme.*
import kotlinx.coroutines.delay

data class SugerenciaItem(
    val nombre: String,
    val descripcion: String
)

@Composable
fun SeleccionEntradasScreen(
    menuViewModel: MenuViewModel,
    entradaViewModel: EntradaViewModel,
    seleccionViewModel: SeleccionEntradasViewModel
) {
    val selectedEntradas = menuViewModel.selectedEntradas
    val showSugerencias = menuViewModel.showSugerencias
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var pendingAutoSelectNombre by remember { mutableStateOf<String?>(null) }

    // Cargar entradas al inicio
    LaunchedEffect(Unit) {
        seleccionViewModel.loadEntradas()
    }

    // Búsqueda fuzzy con debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            seleccionViewModel.resetSearch()
        } else {
            delay(300)
            seleccionViewModel.searchEntradas(searchQuery)
        }
    }

    val entradasState = seleccionViewModel.entradasState

    // Auto-seleccionar elemento recién creado cuando la lista se refresca
    LaunchedEffect(entradasState) {
        val nombre = pendingAutoSelectNombre ?: return@LaunchedEffect
        if (entradasState is State.Success) {
            val dto = entradasState.data.find { it.nombre == nombre }
            dto?.let {
                menuViewModel.updateEntradas(menuViewModel.selectedEntradas + it)
                pendingAutoSelectNombre = null
            }
        }
    }

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

    val searchResults = seleccionViewModel.searchResults
    
    // Filtrar para mostrar solo los NO seleccionados en la lista principal
    val noSeleccionadasFiltradas = searchResults.filter { e -> selectedEntradas.none { s -> s.id == e.id } }

    val sinResultados = searchQuery.isNotBlank() && noSeleccionadasFiltradas.isEmpty()

    // Bottom sheet para añadir nueva entrada
    if (showBottomSheet) {
        NuevaEntradaBottomSheet(
            viewModel = entradaViewModel,
            onDismiss = {
                entradaViewModel.resetForm()
                showBottomSheet = false
            },
            onSuccess = {
                val nombre = (entradaViewModel.formFields.fields["entnom"] as? TextFieldValue)?.text?.trim() ?: ""
                if (nombre.isNotEmpty()) pendingAutoSelectNombre = nombre
                entradaViewModel.resetForm()
                seleccionViewModel.loadEntradas()
                showBottomSheet = false
                searchQuery = ""
            },
            onSelectExisting = { dto ->
                menuViewModel.updateEntradas(menuViewModel.selectedEntradas + dto)
                entradaViewModel.resetForm()
                showBottomSheet = false
                searchQuery = ""
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

            // Sugerencias inteligentes (sólo si el plato no está seleccionado)
            if (showSugerencias) {
                val sugerenciasVisibles = sugerencias.filter { sug -> selectedEntradas.none { it.nombre == sug.nombre } }
                if (sugerenciasVisibles.isNotEmpty()) {
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
                                        Text(text = stringResource(id = R.string.nuevo_sugerencias), fontWeight = FontWeight.Bold, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    items(sugerenciasVisibles) { sugerencia ->
                                        SugerenciaCard(
                                            sugerencia = sugerencia,
                                            onAdd = {
                                                val dto = todasLasEntradas.find { it.nombre == sugerencia.nombre }
                                                dto?.let { menuViewModel.updateEntradas(menuViewModel.selectedEntradas + it) }
                                            }
                                        )
                                    }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = White,
                            shape = RoundedCornerShape(CornerRadiusMedium)
                        ),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.nuevo_buscar_entradas),
                            color = HeavyGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = HeavyGray
                        )
                    },
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.surfaceVariant
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
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                is State.Error -> {
                    item {
                        ErrorBanner(
                            message = entradasState.message,
                            modifier = Modifier.padding(vertical = SpacingSmall)
                        )
                    }
                }
                is State.Success -> {
                    // Añadir nueva solo si no hay resultados en ninguna de las dos listas
                    if (sinResultados) {
                        item {
                            AnadirNuevaListItem(
                                onClick = {
                                    entradaViewModel.loadTiposEntrada()
                                    entradaViewModel.initForCreate()
                                    entradaViewModel.updateField("entnom", TextFieldValue(searchQuery))
                                    showBottomSheet = true
                                }
                            )
                        }
                    }

                    // Sólo mostramos las NO seleccionadas (las seleccionadas están en el BottomSheet)
                    items(noSeleccionadasFiltradas, key = { it.id }) { entrada ->
                        EntradaListItem(
                            entrada = entrada,
                            imageUrl = seleccionViewModel.imagenesMap[entrada.imagenId],
                            isSelected = false,
                            onToggle = { checked ->
                                if (checked) {
                                    menuViewModel.updateEntradas(selectedEntradas + entrada)
                                }
                            }
                        )
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
fun SelectedEntradasBottomSheetContent(
    entradas: List<EntradaResponseDto>,
    imagenesMap: Map<Int, String>,
    onRemove: (EntradaResponseDto) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    // Estado para seguir el desplazamiento del ítem arrastrado
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffset by remember { mutableFloatStateOf(0f) }
    val listSize by rememberUpdatedState(entradas.size)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp)
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        itemsIndexed(entradas, key = { _, entrada -> entrada.id }) { index, entrada ->
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
                        imageUrl = imagenesMap[entrada.imagenId],
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(SpacingMedium))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entrada.nombre.toSmartUpperCase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = TextSizeXSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = entrada.descripcion.toSmartUpperCase(),
                            fontSize = TextSizeXXSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Manija de Arrastre (Drag Handle)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .pointerInput(entrada.id) {
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

                    IconButton(onClick = { onRemove(entrada) }) {
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
private fun EntradaListItem(
    entrada: EntradaResponseDto,
    imageUrl: String?,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
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
            CustomImageView(imageUrl = imageUrl)
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entrada.nombre.toSmartUpperCase(), fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = entrada.descripcion.toSmartUpperCase(), fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
private fun AnadirNuevaListItem(onClick: () -> Unit) {
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
                text = stringResource(R.string.anadir_nueva),
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

private class FakeEntradaRepository : IEntradaRepository {
    private val all = listOf(
        EntradaResponseDto(id = 1, nombre = "Ceviche Clásico", descripcion = "Fresco y ligero", estadoId = 1, tipoEntradaId = 1, imagenId = null, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        EntradaResponseDto(id = 2, nombre = "Carpaccio de Betabel", descripcion = "Con parmesano y limón", estadoId = 1, tipoEntradaId = 1, imagenId = null, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        EntradaResponseDto(id = 3, nombre = "Sopa Azteca", descripcion = "Perfecta para días fríos", estadoId = 1, tipoEntradaId = 1, imagenId = null, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
    )
    override suspend fun getEntradas() = all
    override suspend fun searchEntradas(query: String) =
        if (query.isBlank()) all else all.filter { it.nombre.contains(query, ignoreCase = true) }
    override suspend fun findSimilarEntradas(nombre: String, excludeId: Int?) = emptyList<EntradaResponseDto>()
    override suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto = throw NotImplementedError()
    override suspend fun updateEntrada(id: Int, request: EntradaUpdateRequestDto): EntradaResponseDto = throw NotImplementedError()
    override suspend fun getTiposEntrada(): List<TipoEntradaResponseDto> = emptyList()
    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String, extension: String): ImagenResponseDto = throw NotImplementedError()
}

private val fakeEntrada = EntradaResponseDto(
    id = 1, nombre = "Ceviche Clásico", descripcion = "Fresco, ligero y sabroso",
    estadoId = 1, tipoEntradaId = 1, imagenId = null, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"
)

private val fakeSugerencia = SugerenciaItem(
    nombre = "Carpaccio de Betabel",
    descripcion = "No se ha servido en 15 días. ¡Es hora de volver!"
)

@Preview(showBackground = true, name = "PasoEntradas - Claro")
@Composable
private fun SeleccionEntradasScreenPreview() {
    val menuVm = remember { MenuViewModel() }
    val entradaVm = remember { EntradaViewModel(FakeEntradaRepository()) }
    val seleccionVm = remember { SeleccionEntradasViewModel(FakeEntradaRepository(), FakeMenuImagenRepository()) }
    MenuAppTheme(darkTheme = false) {
        SeleccionEntradasScreen(menuViewModel = menuVm, entradaViewModel = entradaVm, seleccionViewModel = seleccionVm)
    }
}

@Preview(showBackground = true, name = "PasoEntradas - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionEntradasScreenDarkPreview() {
    val menuVm = remember { MenuViewModel() }
    val entradaVm = remember { EntradaViewModel(FakeEntradaRepository()) }
    val seleccionVm = remember { SeleccionEntradasViewModel(FakeEntradaRepository(), FakeMenuImagenRepository()) }
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEntradasScreen(menuViewModel = menuVm, entradaViewModel = entradaVm, seleccionViewModel = seleccionVm)
        }
    }
}

@Preview(showBackground = true, name = "EntradaListItem - Seleccionado")
@Composable
private fun EntradaListItemSelectedPreview() {
    MenuAppTheme(darkTheme = false) {
        EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = true, onToggle = {})
    }
}

@Preview(showBackground = true, name = "EntradaListItem - No seleccionado")
@Composable
private fun EntradaListItemUnselectedPreview() {
    MenuAppTheme(darkTheme = false) {
        EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = false, onToggle = {})
    }
}

@Preview(showBackground = true, name = "EntradaListItem - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaListItemDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = false, onToggle = {})
        }
    }
}

@Preview(showBackground = true, name = "AnadirNuevaListItem - Claro")
@Composable
private fun AnadirNuevaListItemPreview() {
    MenuAppTheme(darkTheme = false) { AnadirNuevaListItem(onClick = {}) }
}

@Preview(showBackground = true, name = "AnadirNuevaListItem - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AnadirNuevaListItemDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AnadirNuevaListItem(onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "SugerenciaCard - Claro")
@Composable
private fun SugerenciaCardPreview() {
    MenuAppTheme(darkTheme = false) { SugerenciaCard(sugerencia = fakeSugerencia, onAdd = {}) }
}

@Preview(showBackground = true, name = "SugerenciaCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SugerenciaCardDarkPreview() {
    MenuAppTheme(darkTheme = true) { SugerenciaCard(sugerencia = fakeSugerencia, onAdd = {}) }
}

@Composable
fun SugerenciaCard(sugerencia: SugerenciaItem, onAdd: () -> Unit) {
    val shape = RoundedCornerShape(CornerRadiusSmall)

    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp),
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sugerencia.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = sugerencia.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    Text(text = stringResource(id = R.string.platos_fondo_anadir), fontSize = TextSizeSmall, lineHeight = TextSizeSmall, fontWeight = FontWeight.Bold, maxLines = 1)
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
