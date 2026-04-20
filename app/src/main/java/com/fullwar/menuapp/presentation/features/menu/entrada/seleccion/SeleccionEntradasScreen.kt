package com.fullwar.menuapp.presentation.features.menu.entrada.seleccion

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.nuevo.NuevaEntradaBottomSheet
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.presentation.features.menu.entrada.seleccion.SeleccionEntradasViewModel
import com.fullwar.menuapp.ui.theme.*

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

    val entradasSeleccionadas = todasLasEntradas.filter { e -> selectedEntradas.any { s -> s.id == e.id } }

    // Seleccionados siempre visibles, sin filtrar por búsqueda
    val seleccionadasFiltradas = entradasSeleccionadas

    val searchResults = seleccionViewModel.searchResults
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

        // Sugerencias inteligentes
        if (showSugerencias) {
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
                            items(sugerencias) { sugerencia ->
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

                // Seleccionados al tope
                items(seleccionadasFiltradas, key = { it.id }) { entrada ->
                    EntradaListItem(
                        entrada = entrada,
                        imageUrl = seleccionViewModel.imagenesMap[entrada.imagenId],
                        isSelected = true,
                        onToggle = { checked ->
                            menuViewModel.updateEntradas(
                                if (checked) selectedEntradas + entrada
                                else selectedEntradas.filter { it.id != entrada.id }.toSet()
                            )
                        }
                    )
                }

                // Separador entre seleccionados y no seleccionados
                if (seleccionadasFiltradas.isNotEmpty()) {
                    item {
                        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.surface)
                    }
                }

                // No seleccionados
                items(noSeleccionadasFiltradas, key = { it.id }) { entrada ->
                    EntradaListItem(
                        entrada = entrada,
                        imageUrl = seleccionViewModel.imagenesMap[entrada.imagenId],
                        isSelected = false,
                        onToggle = { checked ->
                            menuViewModel.updateEntradas(
                                if (checked) selectedEntradas + entrada
                                else selectedEntradas.filter { it.id != entrada.id }.toSet()
                            )
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
                Text(text = entrada.nombre.toSmartUpperCase(), fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = entrada.descripcion.toSmartUpperCase(), fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(text = sugerencia.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, color = MaterialTheme.colorScheme.onBackground)
                Text(text = sugerencia.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
