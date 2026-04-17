package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.PlatoUpdateRequestDto
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.nuevo.NuevoPlatoBottomSheet
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.ui.theme.*

data class SugerenciaPlatoItem(
    val nombre: String,
    val descripcion: String
)

@Composable
fun PasoPlatosFondoScreen(
    menuViewModel: MenuViewModel,
    platoViewModel: PlatoViewModel
) {
    val selectedPlatos = menuViewModel.selectedPlatosFuertes
    val showSugerencias = menuViewModel.showSugerencias
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Cargar platos al inicio
    LaunchedEffect(Unit) {
        platoViewModel.loadPlatos()
    }

    val platosState = platoViewModel.platosState

    val sugerenciasMock = listOf(
        SugerenciaPlatoItem("Lomo Saltado", "Ideal con la Sopa del día."),
        SugerenciaPlatoItem("Ceviche", "Opción fresca para hoy."),
        SugerenciaPlatoItem("Ají de Gallina", "Clásico y contundente."),
        SugerenciaPlatoItem("Tallarines Verdes", "Acompañado de apanado."),
        SugerenciaPlatoItem("Arroz con Mariscos", "Sabor a mar del día.")
    )

    val sugerencias = remember(selectedPlatos) {
        val startIndex = (selectedPlatos.size * 2) % sugerenciasMock.size
        val end = minOf(startIndex + 3, sugerenciasMock.size)
        val selected = sugerenciasMock.subList(startIndex, end)
        if (selected.size < 3) (selected + sugerenciasMock.take(3 - selected.size)) else selected
    }

    val todosLosPlatos = when (platosState) {
        is State.Success -> platosState.data
        else -> emptyList()
    }

    val platosSeleccionados = todosLosPlatos.filter { it.nombre in selectedPlatos }
    val platosNoSeleccionados = todosLosPlatos.filter { it.nombre !in selectedPlatos }

    val seleccionadosFiltrados = platosSeleccionados
    val noSeleccionadosFiltrados = if (searchQuery.isBlank()) platosNoSeleccionados
        else platosNoSeleccionados.filter { it.nombre.contains(searchQuery, ignoreCase = true) }

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
                platoViewModel.initForCreate()
                showBottomSheet = false
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
            // Sugerencias Inteligentes — solo si hay platos cargados
            if (showSugerencias && todosLosPlatos.isNotEmpty()) {
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
                                        menuViewModel.updatePlatosFuertes(selectedPlatos + item.nombre)
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
                        Text(
                            text = platosState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = TextSizeSmall,
                            modifier = Modifier.padding(vertical = SpacingMedium)
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
                                    showBottomSheet = true
                                }
                            )
                        }
                    }

                    // Seleccionados al tope
                    items(seleccionadosFiltrados, key = { it.id }) { plato ->
                        PlatoDisponibleCard(
                            plato = plato,
                            isSelected = true,
                            onToggle = { checked ->
                                menuViewModel.updatePlatosFuertes(
                                    if (checked) selectedPlatos + plato.nombre
                                    else selectedPlatos - plato.nombre
                                )
                            }
                        )
                    }

                    // Separador entre seleccionados y no seleccionados
                    if (seleccionadosFiltrados.isNotEmpty()) {
                        item {
                            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.surface)
                        }
                    }

                    // No seleccionados
                    items(noSeleccionadosFiltrados, key = { it.id }) { plato ->
                        PlatoDisponibleCard(
                            plato = plato,
                            isSelected = false,
                            onToggle = { checked ->
                                menuViewModel.updatePlatosFuertes(
                                    if (checked) selectedPlatos + plato.nombre
                                    else selectedPlatos - plato.nombre
                                )
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
                Text(text = item.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium, color = MaterialTheme.colorScheme.onBackground)
                Text(text = item.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurface)
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

@Composable
fun PlatoDisponibleCard(plato: PlatoResponseDto, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
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
            CustomImageView(imagenId = plato.imagenId, sizeDp = 60)
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plato.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = plato.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

private class FakePlatoRepository : IPlatoRepository {
    override suspend fun getPlatos() = listOf(
        PlatoResponseDto(id = 1, nombre = "Pollo a la Plancha", descripcion = "Con ensalada fresca o papas", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        PlatoResponseDto(id = 2, nombre = "Salmón al Horno", descripcion = "Acompañado de espárragos", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
        PlatoResponseDto(id = 3, nombre = "Seco de Res", descripcion = "Clásico con frijoles y arroz", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
    )
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
private fun PasoPlatosFondoScreenPreview() {
    val menuVm = remember { MenuViewModel() }
    val platoVm = remember { PlatoViewModel(FakePlatoRepository()) }
    MenuAppTheme(darkTheme = false) {
        PasoPlatosFondoScreen(menuViewModel = menuVm, platoViewModel = platoVm)
    }
}

@Preview(showBackground = true, name = "PasoPlatosFondo - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PasoPlatosFondoScreenDarkPreview() {
    val menuVm = remember { MenuViewModel() }
    val platoVm = remember { PlatoViewModel(FakePlatoRepository()) }
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PasoPlatosFondoScreen(menuViewModel = menuVm, platoViewModel = platoVm)
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
        PlatoDisponibleCard(plato = fakePlatoResponseDto, isSelected = true, onToggle = {})
    }
}

@Preview(showBackground = true, name = "PlatoDisponibleCard - No seleccionado")
@Composable
private fun PlatoDisponibleCardUnselectedPreview() {
    MenuAppTheme(darkTheme = false) {
        PlatoDisponibleCard(plato = fakePlatoResponseDto, isSelected = false, onToggle = {})
    }
}

@Preview(showBackground = true, name = "PlatoDisponibleCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoDisponibleCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlatoDisponibleCard(plato = fakePlatoResponseDto, isSelected = false, onToggle = {})
        }
    }
}
