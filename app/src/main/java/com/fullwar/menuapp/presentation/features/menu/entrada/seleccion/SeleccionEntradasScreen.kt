package com.fullwar.menuapp.presentation.features.menu.entrada.seleccion

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.*
import com.fullwar.menuapp.di.Constants
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.components.ItemListSkeleton
import com.fullwar.menuapp.presentation.common.components.ConfirmDeleteBottomSheet
import com.fullwar.menuapp.presentation.common.components.SwipeAction
import com.fullwar.menuapp.presentation.common.components.SwipeableActionsContainer
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.editar.EditarEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.nuevo.NuevaEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.fullwar.menuapp.ui.theme.*
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
    // val showSugerencias = menuViewModel.showSugerencias
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var pendingAutoSelectNombre by remember { mutableStateOf<String?>(null) }
    var openedEntradaId by remember { mutableStateOf<Int?>(null) }
    var entradaToEdit by remember { mutableStateOf<EntradaResponseDto?>(null) }
    var entradaToDelete by remember { mutableStateOf<EntradaResponseDto?>(null) }
    val context = LocalContext.current
    val toastElementosNoDisponibles = stringResource(R.string.toast_elementos_no_disponibles)

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

    LaunchedEffect(entradasState) {
        if (entradasState !is State.Success) return@LaunchedEffect
        val nombre = pendingAutoSelectNombre
        if (nombre != null) {
            val dto = entradasState.data.find { it.nombre == nombre }
            dto?.let {
                menuViewModel.updateEntradas(menuViewModel.selectedEntradas + it)
                pendingAutoSelectNombre = null
            }
        }
        val freshById = entradasState.data.associateBy { it.id }
        val current = menuViewModel.selectedEntradas
        val patched = current.mapNotNull { freshById[it.id] }
        if (patched != current) {
            menuViewModel.updateEntradas(patched)
            if (patched.size < current.size) {
                Toast.makeText(context, toastElementosNoDisponibles, Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
    val sugerencias = remember {
        listOf(
            SugerenciaItem("Carpaccio de Betabel", "No se ha servido en 15 días. ¡Es hora de volver!"),
            SugerenciaItem("Ceviche Clásico", "Combina perfectamente con platos de pescado."),
            SugerenciaItem("Sopa Azteca", "Perfecta para días fríos."),
            SugerenciaItem("Bruschetta de Jitomate", "Opción ligera y fresca."),
            SugerenciaItem("Tabla de Quesos", "Ideal para compartir en la mesa.")
        )
    }
    */

    val todasLasEntradas = when (entradasState) {
        is State.Success -> entradasState.data
        else -> emptyList()
    }

    val searchResults = seleccionViewModel.searchResults

    val sinResultados = searchQuery.isNotBlank() && searchResults.isEmpty()

    // Bottom sheet para añadir nueva entrada
    if (showBottomSheet) {
        NuevaEntradaBottomSheet(
            viewModel = entradaViewModel,
            initialNombre = searchQuery,
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

    entradaToEdit?.let { entrada ->
        EditarEntradaBottomSheet(
            viewModel = entradaViewModel,
            entrada = entrada,
            onDismiss = {
                entradaViewModel.resetForm()
                entradaToEdit = null
            },
            onSuccess = {
                entradaToEdit = null
                seleccionViewModel.loadEntradas()
            }
        )
    }

    entradaToDelete?.let { entrada ->
        ConfirmDeleteBottomSheet(
            title = stringResource(R.string.dialog_eliminar_entrada_titulo, entrada.nombre),
            message = stringResource(R.string.dialog_eliminar_entrada_mensaje),
            confirmLabel = stringResource(R.string.dialog_eliminar_confirmar),
            dismissLabel = stringResource(R.string.calendar_cancel),
            onConfirm = {
                seleccionViewModel.deleteEntrada(entrada.id)
                entradaToDelete = null
            },
            onDismiss = { entradaToDelete = null }
        )
    }

    val listState = rememberLazyListState()
    val canScrollDown by remember { derivedStateOf { listState.canScrollForward } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge)
    ) {
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )

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

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = SpacingMedium,
                    bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding() + SpacingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(SpacingMedium)
            ) {
                when (entradasState) {
                    is State.Loading -> {
                        items(5) {
                            ItemListSkeleton()
                        }
                    }
                    is State.Error -> {
                        item {
                            ErrorBanner(
                                message = entradasState.message,
                                modifier = Modifier.padding(vertical = SpacingSmall),
                                onRetry = { seleccionViewModel.loadEntradas() }
                            )
                        }
                    }
                    is State.Success -> {
                        if (sinResultados) {
                            item {
                                AnadirNuevaListItem(
                                    onClick = {
                                        entradaViewModel.loadTiposEntrada()
                                        entradaViewModel.initForCreate()
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }

                        items(searchResults, key = { it.id }) { entrada ->
                            EntradaListItem(
                                entrada = entrada,
                                imageUrl = entrada.imagenId?.let { "${Constants.BASE_URL}/api/imagen/$it/contenido" },
                                isSelected = selectedEntradas.any { it.id == entrada.id },
                                isSwipeOpen = openedEntradaId == entrada.id,
                                onOpen = { openedEntradaId = entrada.id },
                                onClose = { openedEntradaId = null },
                                onToggle = { checked ->
                                    if (checked) {
                                        menuViewModel.updateEntradas(selectedEntradas + entrada)
                                    } else {
                                        menuViewModel.updateEntradas(selectedEntradas - entrada)
                                    }
                                },
                                actions = listOf(
                                    SwipeAction(
                                        icon = Icons.Filled.Edit,
                                        contentDescription = "Editar",
                                        backgroundColor = MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            entradaViewModel.loadTiposEntrada()
                                            entradaViewModel.initForEdit(entrada)
                                            entradaToEdit = entrada
                                            openedEntradaId = null
                                        }
                                    ),
                                    SwipeAction(
                                        icon = Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        backgroundColor = DangerRed,
                                        onClick = {
                                            entradaToDelete = entrada
                                            openedEntradaId = null
                                        }
                                    )
                                )
                            )
                        }

                        if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                            item {
                                AnadirNuevaListItem(
                                    onClick = {
                                        entradaViewModel.loadTiposEntrada()
                                        entradaViewModel.initForCreate()
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            if (canScrollDown) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(ButtonHeightLarge)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun SelectedEntradasBottomSheetContent(
    entradas: List<EntradaResponseDto>,
    onRemove: (EntradaResponseDto) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Los ítems arrastrables ocupan los índices 0..n-1; el Spacer final no es
        // ReorderableItem, así que from.index/to.index mapean directo a la lista.
        onMove(from.index, to.index)
        haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = ListMaxHeight)
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
    ) {
        itemsIndexed(entradas, key = { _, entrada -> entrada.id }) { _, entrada ->
            ReorderableItem(reorderableState, key = entrada.id) { isDragging ->
                Surface(
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    color = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                    tonalElevation = if (isDragging) SpacingSmall else 0.dp,
                    shadowElevation = if (isDragging) SpacingSmall else 0.dp,
                    border = BorderStroke(
                        StrokeWidthThin,
                        if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(SpacingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomImageView(
                            imageUrl = entrada.imagenId?.let { "${Constants.BASE_URL}/api/imagen/$it/contenido" },
                            modifier = Modifier.size(Spacing3XLarge)
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

                        // Manija de Arrastre: el arrastre inicia al tocar y mover (sin long-press)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                    },
                                    onDragStopped = {
                                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    }
                                ),
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
        }
        item { Spacer(modifier = Modifier.height(SpacingSmall)) }
    }
}

@Composable
private fun EntradaListItem(
    entrada: EntradaResponseDto,
    imageUrl: String?,
    isSelected: Boolean,
    isSwipeOpen: Boolean,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    onToggle: (Boolean) -> Unit,
    actions: List<SwipeAction>
) {
    var isExpanded by remember { mutableStateOf(false) }
    var nombreOverflows by remember { mutableStateOf(false) }
    var descripcionOverflows by remember { mutableStateOf(false) }
    val hasOverflow = nombreOverflows || descripcionOverflows
    val minTextColumnHeight = ((LineHeightNombre.value * 2 + LineHeightDescripcion.value) * LocalDensity.current.fontScale).dp

    SwipeableActionsContainer(
        isOpen = isSwipeOpen,
        onOpen = onOpen,
        onClose = onClose,
        actions = actions
    ) {
        Surface(
            shape = RoundedCornerShape(0.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { onToggle(!isSelected) }
        ) {
            Row(
                modifier = Modifier.padding(SpacingMedium),
                verticalAlignment = Alignment.Top
            ) {
                CustomImageView(imageUrl = imageUrl, modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(SpacingMedium))
                Column(modifier = Modifier.weight(1f).heightIn(min = minTextColumnHeight).padding(end = SpacingSmall)) {
                    Text(
                        text = entrada.nombre.toSmartUpperCase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeMedium,
                        lineHeight = LineHeightNombre,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            if (!isExpanded) nombreOverflows = result.hasVisualOverflow
                        }
                    )
                    if (!isExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = SpacingXSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entrada.descripcion.toSmartUpperCase(),
                                fontSize = TextSizeXSmall,
                                lineHeight = LineHeightDescripcion,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { result -> descripcionOverflows = result.hasVisualOverflow },
                                modifier = Modifier.weight(1f)
                            )
                            if (hasOverflow) {
                                Text(
                                    text = stringResource(R.string.ver_mas),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = TextSizeXXSmall,
                                    lineHeight = LineHeightDescripcion,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .padding(start = SpacingXSmall)
                                        .clickable(onClick = { isExpanded = true })
                                )
                            }
                        }
                    } else {
                        Text(
                            text = entrada.descripcion.toSmartUpperCase(),
                            fontSize = TextSizeXSmall,
                            lineHeight = LineHeightDescripcion,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = Int.MAX_VALUE,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier.padding(top = SpacingXSmall)
                        )
                        if (hasOverflow) {
                            Text(
                                text = stringResource(R.string.ver_menos),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = TextSizeXXSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clickable(onClick = { isExpanded = false })
                            )
                        }
                    }
                }
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = HeavyGray
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    } // SwipeableActionsContainer
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

private class FakeMenuDiarioRepository : IMenuDiarioRepository {
    override suspend fun createMenuDiario(request: com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto, imagenFile: java.io.File?) = 0
    override suspend fun getMenusDiarios(busqueda: String?) = emptyList<com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto>()
    override suspend fun getMenuDiarioById(id: Int) = throw NotImplementedError()
    override suspend fun updateMenuDiario(id: Int, request: com.fullwar.menuapp.data.model.MenuDiarioUpdateRequestDto, imagenFile: java.io.File?) = false
    override suspend fun deleteMenuDiario(id: Int) {}
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
    override suspend fun updateEntrada(request: EntradaUpdateRequestDto) = throw NotImplementedError()
    override suspend fun deleteEntrada(id: Int) {}
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
    val menuVm = remember { MenuViewModel(FakeMenuDiarioRepository()) }
    val entradaVm = remember { EntradaViewModel(FakeEntradaRepository()) }
    val seleccionVm = remember { SeleccionEntradasViewModel(FakeEntradaRepository()) }
    MenuAppTheme(darkTheme = false) {
        SeleccionEntradasScreen(menuViewModel = menuVm, entradaViewModel = entradaVm, seleccionViewModel = seleccionVm)
    }
}

@Preview(showBackground = true, name = "PasoEntradas - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionEntradasScreenDarkPreview() {
    val menuVm = remember { MenuViewModel(FakeMenuDiarioRepository()) }
    val entradaVm = remember { EntradaViewModel(FakeEntradaRepository()) }
    val seleccionVm = remember { SeleccionEntradasViewModel(FakeEntradaRepository()) }
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
        EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = true,
            isSwipeOpen = false, onOpen = {}, onClose = {}, onToggle = {}, actions = emptyList())
    }
}

@Preview(showBackground = true, name = "EntradaListItem - No seleccionado")
@Composable
private fun EntradaListItemUnselectedPreview() {
    MenuAppTheme(darkTheme = false) {
        EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = false,
            isSwipeOpen = false, onOpen = {}, onClose = {}, onToggle = {}, actions = emptyList())
    }
}

@Preview(showBackground = true, name = "EntradaListItem - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaListItemDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EntradaListItem(entrada = fakeEntrada, imageUrl = null, isSelected = false,
                isSwipeOpen = false, onOpen = {}, onClose = {}, onToggle = {}, actions = emptyList())
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
            .width(CardMaxWidth)
            .height(CardImageHeight),
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
                    modifier = Modifier.heightIn(min = SpacingXXLarge)
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
