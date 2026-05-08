package com.fullwar.menuapp.presentation.features.home.tabs.historial

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import androidx.compose.ui.layout.ContentScale
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto
import com.fullwar.menuapp.data.model.TipoPlatoCountDto
import com.fullwar.menuapp.presentation.common.components.CustomImageView
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.components.ImagenFondoPreviewDialog
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.home.tabs.historial.HistorialViewModel
import com.fullwar.menuapp.ui.theme.*
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun HistorialTab(
    modifier: Modifier = Modifier,
    onNuevoMenuClick: (dateMillis: Long, conflictoMenuId: Int?) -> Unit = { _, _ -> },
    onEditarMenuClick: (Int) -> Unit = {},
    viewModel: HistorialViewModel = koinViewModel()
) {
    HistorialTabContent(
        modifier = modifier,
        onNuevoMenuClick = onNuevoMenuClick,
        onEliminarMenu = viewModel::eliminarMenu,
        onEditarMenu = onEditarMenuClick,
        menusState = viewModel.menusState,
        displayMenus = viewModel.displayMenus,
        onLoadMenus = viewModel::loadMenus,
        onSearch = viewModel::searchMenus,
        onResetSearch = viewModel::resetSearch,
        onFilterByDate = viewModel::filterByDate,
        onFilterByDateRange = viewModel::filterByDateRange,
        onClearDateFilter = viewModel::clearDateFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTabContent(
    modifier: Modifier = Modifier,
    onNuevoMenuClick: (dateMillis: Long, conflictoMenuId: Int?) -> Unit = { _, _ -> },
    onEliminarMenu: (Int) -> Unit = {},
    onEditarMenu: (Int) -> Unit = {},
    menusState: State<List<MenuDiarioListItemResponseDto>>,
    displayMenus: List<MenuDiarioListItemResponseDto>,
    onLoadMenus: () -> Unit,
    onSearch: (String) -> Unit,
    onResetSearch: () -> Unit,
    onFilterByDate: (Long?) -> Unit,
    onFilterByDateRange: (Long?, Long?) -> Unit,
    onClearDateFilter: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var isRangeMode by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedEndDateMillis by remember { mutableStateOf<Long?>(null) }

    var showNuevoMenuPicker by remember { mutableStateOf(false) }
    var pendingNuevoDateMillis by remember { mutableStateOf<Long?>(null) }
    var conflictoMenu by remember { mutableStateOf<MenuDiarioListItemResponseDto?>(null) }

    val todayUtcMillis = remember {
        val localCal = Calendar.getInstance()
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCal.clear()
        utcCal.set(
            localCal.get(Calendar.YEAR),
            localCal.get(Calendar.MONTH),
            localCal.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
        utcCal.timeInMillis
    }

    //val filters = listOf("Hoy", "Última Semana", "Platos Principales")
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es")) }

    LifecycleResumeEffect(Unit) {
        onLoadMenus()
        onPauseOrDispose { }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            onResetSearch()
        } else {
            delay(300)
            onSearch(searchQuery)
        }
    }

    val activeDateChipText = remember(selectedDateMillis, selectedStartDateMillis, selectedEndDateMillis) {
        when {
            selectedDateMillis != null -> dateFormat.format(Date(selectedDateMillis!!))
            selectedStartDateMillis != null && selectedEndDateMillis != null ->
                "${dateFormat.format(Date(selectedStartDateMillis!!))} - ${dateFormat.format(Date(selectedEndDateMillis!!))}"
            else -> null
        }
    }

    val hasDateFilter = activeDateChipText != null

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            isRangeMode = selectedStartDateMillis != null && selectedEndDateMillis != null
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            isRangeMode = isRangeMode,
            onRangeModeChange = { isRangeMode = it },
            initialSelectedDateMillis = selectedDateMillis,
            initialStartDateMillis = selectedStartDateMillis,
            initialEndDateMillis = selectedEndDateMillis,
            onApply = { singleDate, startDate, endDate ->
                selectedFilter = ""
                if (isRangeMode) {
                    selectedStartDateMillis = startDate
                    selectedEndDateMillis = endDate
                    selectedDateMillis = null
                    onFilterByDateRange(startDate, endDate)
                } else {
                    selectedDateMillis = singleDate
                    selectedStartDateMillis = null
                    selectedEndDateMillis = null
                    onFilterByDate(singleDate)
                }
                showDatePicker = false
            },
            onClear = {
                selectedDateMillis = null
                selectedStartDateMillis = null
                selectedEndDateMillis = null
                onClearDateFilter()
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.historial_titulo),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeXLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = if (hasDateFilter) MaterialTheme.colorScheme.surfaceVariant
                                   else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNuevoMenuPicker = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(CornerRadiusLarge)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(text = stringResource(id = R.string.historial_nuevo_menu), fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (showNuevoMenuPicker) {
            MenuFechaPickerDialog(
                onConfirm = { dateMillis ->
                    showNuevoMenuPicker = false
                    val existente = displayMenus.firstOrNull { isSameDayUtc(it.fecha, dateMillis) }
                    if (existente != null) {
                        pendingNuevoDateMillis = dateMillis
                        conflictoMenu = existente
                    } else {
                        onNuevoMenuClick(dateMillis, null)
                    }
                },
                onDismiss = { showNuevoMenuPicker = false }
            )
        }

        conflictoMenu?.let { menu ->
            MenuConflictoDialog(
                menu = menu,
                onEditar = {
                    conflictoMenu = null
                    pendingNuevoDateMillis = null
                    onEditarMenu(menu.id)
                },
                onCrearNuevo = {
                    val dateMillis = pendingNuevoDateMillis!!
                    val conflictoId = menu.id
                    conflictoMenu = null
                    pendingNuevoDateMillis = null
                    onNuevoMenuClick(dateMillis, conflictoId)
                },
                onCancelar = {
                    conflictoMenu = null
                    pendingNuevoDateMillis = null
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = SpacingLarge)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.historial_buscar), color = HeavyGray) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = HeavyGray) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = HeavyGray)
                        }
                    } else {
                        Icon(imageVector = Icons.Filled.Tune, contentDescription = null, tint = HeavyGray)
                    }
                },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = HeavyGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(SpacingMedium))
            /*
            Row(horizontalArrangement = Arrangement.spacedBy(SpacingSmall)) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedFilter = ""
                                selectedDateMillis = null
                                selectedStartDateMillis = null
                                selectedEndDateMillis = null
                                onClearDateFilter()
                            } else {
                                selectedFilter = filter
                                when (filter) {
                                    "Hoy" -> {
                                        selectedDateMillis = todayUtcMillis
                                        selectedStartDateMillis = null
                                        selectedEndDateMillis = null
                                        onFilterByDate(todayUtcMillis)
                                    }
                                    "Última Semana" -> {
                                        val start = todayUtcMillis - 6L * 24 * 60 * 60 * 1000
                                        selectedStartDateMillis = start
                                        selectedEndDateMillis = todayUtcMillis
                                        selectedDateMillis = null
                                        onFilterByDateRange(start, todayUtcMillis)
                                    }
                                    else -> {}
                                }
                            }
                        },
                        label = { Text(text = filter) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(IconSizeSmall)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                            labelColor = MaterialTheme.colorScheme.onBackground,
                            iconColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
            */
            if (activeDateChipText != null) {
                Spacer(modifier = Modifier.height(SpacingSmall))
                FilterChip(
                    selected = true,
                    onClick = { showDatePicker = true },
                    label = { Text(text = activeDateChipText) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(IconSizeSmall))
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                selectedDateMillis = null
                                selectedStartDateMillis = null
                                selectedEndDateMillis = null
                                selectedFilter = ""
                                onClearDateFilter()
                            },
                            modifier = Modifier.size(IconSizeSmall)
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(IconSizeSmall))
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White,
                        selectedTrailingIconColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(SpacingLarge))

            Text(
                text = stringResource(id = R.string.historial_recientes),
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeSmall,
                color = HeavyGray
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            when (menusState) {
                is State.Loading -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                        items(5) {
                            MenuHistorialCardSkeleton()
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                is State.Error -> {
                    ErrorBanner(
                        message = menusState.message,
                        modifier = Modifier.padding(vertical = SpacingSmall),
                        onRetry = { onLoadMenus() }
                    )
                }
                is State.Success -> {
                    if (displayMenus.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (searchQuery.isNotBlank() || hasDateFilter)
                                    "Sin resultados para los filtros aplicados"
                                else
                                    "No hay menús registrados aún",
                                color = HeavyGray,
                                fontSize = TextSizeMedium
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                            items(displayMenus, key = { it.id }) { menu ->
                                MenuHistorialCard(
                                    menu = menu,
                                    onEliminarMenu = { onEliminarMenu(menu.id) },
                                    onEditarMenu = { onEditarMenu(menu.id) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MenuHistorialCard(
    menu: MenuDiarioListItemResponseDto,
    onEliminarMenu: () -> Unit = {},
    onEditarMenu: () -> Unit = {}
) {
    var showOpcionesSheet by remember { mutableStateOf(false) }
    var showImagenPreview by remember { mutableStateOf(false) }
    var showConfirmEliminar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val platosResumen = menu.cantidadPlatos
        .filter { it.cantidad > 0 }
        .joinToString(" · ") {
            val label = if (it.cantidad == 1) "plato de" else "platos de"
            "${it.cantidad} $label ${it.tipoPlato}"
        }
        .ifBlank { "Sin platos" }

    val entradasResumen = when (menu.cantidadEntradas) {
        0 -> "Sin entradas"
        1 -> "1 entrada"
        else -> "${menu.cantidadEntradas} entradas"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomImageView(
            imageUrl = menu.imagenUrl,
            contentScale = ContentScale.Fit,
            defaultImageRes = R.drawable.default_image_menu,
            modifier = if (menu.imagenUrl != null) {
                Modifier.clickable { showImagenPreview = true }
            } else {
                Modifier
            }
        )

        Spacer(modifier = Modifier.width(SpacingMedium))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = menu.descripcionFecha, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)

            Text(text = platosResumen, fontSize = TextSizeSmall, color = HeavyGray, maxLines = 1)
            Text(text = entradasResumen, fontSize = TextSizeSmall, color = HeavyGray, maxLines = 1)

            if (!menu.coincidencias.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.padding(top = SpacingXSmall),
                    verticalArrangement = Arrangement.spacedBy(SpacingXSmall)
                ) {
                    menu.coincidencias.forEach { c ->
                        Surface(
                            shape = RoundedCornerShape(CornerRadiusSmall),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = c.nombre,
                                fontSize = TextSizeXXSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(
                                    horizontal = SpacingSmall,
                                    vertical = 2.dp
                                )
                            )
                        }
                    }
                }
            }

            if (menu.tiempoTranscurrido.isNotBlank()) {
                Text(text = menu.tiempoTranscurrido, fontSize = TextSizeXSmall, color = HeavyGray)
            }
        }

        IconButton(onClick = { showOpcionesSheet = true }) {
            Surface(modifier = Modifier.size(IconSize3XLarge), color = MaterialTheme.colorScheme.background) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(SpacingSmall)
                )
            }
        }
    }
    HorizontalDivider(color = HeavyGray)

    if (showOpcionesSheet) {
        MenuHistorialOpcionesSheet(
            menu = menu,
            onDismiss = { showOpcionesSheet = false },
            onVerCarta = { showImagenPreview = true },
            onEditarMenu = { onEditarMenu() },
            onCompartir = {
                menu.imagenUrl?.let { url ->
                    scope.launch {
                        val file = descargarImagenACache(context, url)
                        if (file != null) compartirImagen(context, file)
                    }
                }
            },
            onEliminar = { showConfirmEliminar = true }
        )
    }

    if (showConfirmEliminar) {
        AlertDialog(
            onDismissRequest = { showConfirmEliminar = false },
            title = { Text("Eliminar menú") },
            text = { Text("¿Estás seguro de que deseas eliminar el menú del ${menu.descripcionFecha}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmEliminar = false
                    onEliminarMenu()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showImagenPreview && menu.imagenUrl != null) {
        ImagenFondoPreviewDialog(
            imagenUrl = menu.imagenUrl,
            onDismiss = { showImagenPreview = false }
        )
    }
}

@Composable
private fun OpcionItem(
    icono: ImageVector,
    texto: String,
    onClick: () -> Unit,
    tintColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SpacingLarge, vertical = SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingLarge)
    ) {
        Icon(imageVector = icono, contentDescription = null, tint = tintColor, modifier = Modifier.size(IconSizeMedium))
        Text(text = texto, fontSize = TextSizeMedium, color = tintColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuHistorialOpcionesSheet(
    menu: MenuDiarioListItemResponseDto,
    onDismiss: () -> Unit,
    onVerCarta: () -> Unit,
    onEditarMenu: () -> Unit,
    onCompartir: () -> Unit,
    onEliminar: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = SpacingXLarge)) {
            Text(
                text = menu.descripcionFecha,
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeMedium,
                color = HeavyGray,
                modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingLarge).padding(bottom = SpacingMedium)
            )
            HorizontalDivider(color = HeavyGray)
            OpcionItem(icono = Icons.Filled.MenuBook, texto = "Ver Carta", onClick = { onVerCarta(); onDismiss() })
            OpcionItem(icono = Icons.Filled.Edit, texto = "Editar Menú", onClick = { onEditarMenu(); onDismiss() })
            OpcionItem(icono = Icons.Filled.Share, texto = "Compartir", onClick = { onCompartir(); onDismiss() })
            OpcionItem(icono = Icons.Filled.Delete, texto = "Eliminar", onClick = { onEliminar(); onDismiss() }, tintColor = MaterialTheme.colorScheme.error)
        }
    }
}

private suspend fun descargarImagenACache(context: android.content.Context, url: String): File? =
    withContext(Dispatchers.IO) {
        try {
            val dir = File(context.cacheDir, "menu_images").also { it.mkdirs() }
            val file = File(dir, "menu_share_${System.currentTimeMillis()}.jpg")
            java.net.URL(url).openStream().use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            file
        } catch (_: Exception) { null }
    }

private fun compartirImagen(context: android.content.Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir menú"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    isRangeMode: Boolean,
    onRangeModeChange: (Boolean) -> Unit,
    initialSelectedDateMillis: Long?,
    initialStartDateMillis: Long?,
    initialEndDateMillis: Long?,
    onApply: (singleDate: Long?, startDate: Long?, endDate: Long?) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val todayUtcMillis = remember {
        val localCalendar = Calendar.getInstance()
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.clear()
        utcCalendar.set(
            localCalendar.get(Calendar.YEAR),
            localCalendar.get(Calendar.MONTH),
            localCalendar.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
        utcCalendar.timeInMillis
    }

    val noFutureDates = remember(todayUtcMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayUtcMillis
            override fun isSelectableYear(year: Int) = year <= Calendar.getInstance().get(Calendar.YEAR)
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = noFutureDates
    )

    var rangeClearKey by remember { mutableIntStateOf(0) }

    val dateRangePickerState = key(rangeClearKey) {
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialStartDateMillis,
            initialSelectedEndDateMillis = initialEndDateMillis,
            selectableDates = noFutureDates
        )
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es")) }

    val selectionText = if (isRangeMode) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis
        when {
            start != null && end != null -> "${dateFormat.format(Date(start))} - ${dateFormat.format(Date(end))}"
            start != null -> "${dateFormat.format(Date(start))} - --/--/----"
            else -> "--/--/---- - --/--/----"
        }
    } else {
        val selected = datePickerState.selectedDateMillis
        if (selected != null) dateFormat.format(Date(selected)) else "--/--/----"
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = SpacingLarge, end = SpacingSmall, top = SpacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.calendar_title), fontWeight = FontWeight.Bold, fontSize = TextSizeXLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = HeavyGray)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingLarge),
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(SpacingMedium)) {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = !isRangeMode,
                                onClick = { onRangeModeChange(false) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    activeBorderColor = MaterialTheme.colorScheme.primary,
                                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    inactiveBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(text = stringResource(id = R.string.calendar_single_date)) }
                            SegmentedButton(
                                selected = isRangeMode,
                                onClick = { onRangeModeChange(true) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    activeBorderColor = MaterialTheme.colorScheme.primary,
                                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    inactiveBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(text = stringResource(id = R.string.calendar_date_range)) }
                        }

                        Spacer(modifier = Modifier.height(SpacingSmall))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = SpacingSmall)) {
                            Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(IconSizeMedium))
                            Spacer(modifier = Modifier.width(SpacingSmall))
                            Text(text = stringResource(id = R.string.calendar_selection_label), fontSize = TextSizeSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(SpacingXSmall))
                            Text(text = selectionText, fontSize = TextSizeSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpacingSmall))
                HorizontalDivider(color = HeavyGray, modifier = Modifier.padding(horizontal = SpacingLarge))

                if (isRangeMode) {
                    DateRangePicker(
                        state = dateRangePickerState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        title = null, headline = null, showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            navigationContentColor = MaterialTheme.colorScheme.onSurface,
                            todayContentColor = MaterialTheme.colorScheme.onBackground,
                            todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                            selectedDayContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                } else {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = SpacingSmall),
                        title = null, headline = null, showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            navigationContentColor = MaterialTheme.colorScheme.onSurface,
                            todayContentColor = MaterialTheme.colorScheme.onBackground,
                            todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                            selectedDayContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                val hasSelection = if (isRangeMode) dateRangePickerState.selectedStartDateMillis != null
                                   else datePickerState.selectedDateMillis != null

                Row(modifier = Modifier.fillMaxWidth().padding(SpacingLarge), horizontalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                    OutlinedButton(
                        onClick = if (hasSelection) {
                            { datePickerState.selectedDateMillis = null; rangeClearKey++; onClear() }
                        } else onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(CornerRadiusMedium)
                    ) {
                        Text(
                            text = stringResource(id = if (hasSelection) R.string.calendar_clear else R.string.calendar_cancel),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = {
                            if (isRangeMode) onApply(null, dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis)
                            else onApply(datePickerState.selectedDateMillis, null, null)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(CornerRadiusMedium),
                        enabled = if (isRangeMode)
                            dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
                        else
                            datePickerState.selectedDateMillis != null
                    ) {
                        Text(text = stringResource(id = R.string.calendar_apply), color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

// --- Previews ---

@Composable
private fun PreviewWrapper(darkTheme: Boolean, content: @Composable () -> Unit) {
    MenuAppTheme(darkTheme = darkTheme) {
        if (darkTheme) Surface(color = MaterialTheme.colorScheme.background) { content() }
        else content()
    }
}

// --- Helpers y composables para creación de menú con fecha ---

private fun isSameDayUtc(fechaDDMMYYYY: String, utcMillis: Long): Boolean =
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        sdf.parse(fechaDDMMYYYY)?.time == utcMillis
    } catch (e: Exception) { false }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuFechaPickerDialog(
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val todayUtcMillis = remember {
        val localCal = Calendar.getInstance()
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCal.clear()
        utcCal.set(localCal.get(Calendar.YEAR), localCal.get(Calendar.MONTH),
                   localCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        utcCal.timeInMillis
    }
    val maxUtcMillis = remember { todayUtcMillis + 365L * 24 * 60 * 60 * 1000 }
    val currentYear  = remember { Calendar.getInstance().get(Calendar.YEAR) }

    val selectableDates = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis in todayUtcMillis..maxUtcMillis
            override fun isSelectableYear(year: Int) = year in currentYear..(currentYear + 1)
        }
    }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = todayUtcMillis,
        selectableDates = selectableDates
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(com.fullwar.menuapp.ui.theme.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = com.fullwar.menuapp.ui.theme.SpacingLarge,
                                 end = com.fullwar.menuapp.ui.theme.SpacingSmall,
                                 top = com.fullwar.menuapp.ui.theme.SpacingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.nuevo_menu_fecha_titulo),
                         fontWeight = FontWeight.Bold,
                         fontSize = com.fullwar.menuapp.ui.theme.TextSizeXLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = null,
                             tint = HeavyGray)
                    }
                }

                DatePicker(
                    state = state,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(horizontal = com.fullwar.menuapp.ui.theme.SpacingSmall),
                    title = null, headline = null, showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        navigationContentColor = MaterialTheme.colorScheme.onSurface,
                        todayContentColor = MaterialTheme.colorScheme.onBackground,
                        todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                        selectedDayContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(com.fullwar.menuapp.ui.theme.SpacingLarge),
                    horizontalArrangement = Arrangement.spacedBy(com.fullwar.menuapp.ui.theme.SpacingMedium)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(com.fullwar.menuapp.ui.theme.CornerRadiusMedium)
                    ) {
                        Text(text = stringResource(id = R.string.calendar_cancel),
                             color = MaterialTheme.colorScheme.onSurface)
                    }
                    Button(
                        onClick = { state.selectedDateMillis?.let { onConfirm(it) } },
                        enabled = state.selectedDateMillis != null,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(com.fullwar.menuapp.ui.theme.CornerRadiusMedium)
                    ) {
                        Text(text = stringResource(R.string.nuevo_menu_fecha_continuar),
                             color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuConflictoDialog(
    menu: MenuDiarioListItemResponseDto,
    onEditar: () -> Unit,
    onCrearNuevo: () -> Unit,
    onCancelar: () -> Unit
) {
    val platosTotal = menu.cantidadPlatos.sumOf { it.cantidad }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.nuevo_menu_conflicto_titulo)) },
        text = {
            Text(stringResource(R.string.nuevo_menu_conflicto_desc,
                 menu.descripcionFecha, menu.cantidadEntradas, platosTotal))
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(com.fullwar.menuapp.ui.theme.SpacingSmall)
            ) {
                Button(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.nuevo_menu_conflicto_editar))
                }
                OutlinedButton(
                    onClick = onCrearNuevo,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.nuevo_menu_conflicto_crear))
                }
                TextButton(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.calendar_cancel))
                }
            }
        }
    )
}

// --- Previews ---

private val previewMenu = MenuDiarioListItemResponseDto(
    id = 1,
    fecha = "24/10/2024",
    descripcionFecha = "Lunes, 24 Oct",
    estadoId = 1,
    cantidadEntradas = 2,
    cantidadPlatos = listOf(TipoPlatoCountDto("Segundos", 3), TipoPlatoCountDto("Postres", 1)),
    tiempoTranscurrido = "Hace 2 días"
)

private val previewMenus = listOf(
    previewMenu,
    MenuDiarioListItemResponseDto(
        id = 2,
        fecha = "23/10/2024",
        descripcionFecha = "Domingo, 23 Oct",
        estadoId = 1,
        cantidadEntradas = 1,
        cantidadPlatos = listOf(TipoPlatoCountDto("Segundos", 2)),
        tiempoTranscurrido = "Hace 3 días"
    ),
    MenuDiarioListItemResponseDto(
        id = 3,
        fecha = "21/10/2024",
        descripcionFecha = "Viernes, 21 Oct",
        estadoId = 1,
        cantidadEntradas = 0,
        cantidadPlatos = listOf(TipoPlatoCountDto("Segundos", 1), TipoPlatoCountDto("Postres", 2)),
        tiempoTranscurrido = "Hace 5 días"
    )
)

// HistorialScreen

@Preview(showBackground = true, name = "HistorialScreen - Lista - Claro")
@Composable
private fun HistorialScreenListaClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        HistorialTabContent(
            menusState = State.Success(previewMenus),
            displayMenus = previewMenus,
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

@Preview(showBackground = true, name = "HistorialScreen - Lista - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HistorialScreenListaOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        HistorialTabContent(
            menusState = State.Success(previewMenus),
            displayMenus = previewMenus,
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

@Preview(showBackground = true, name = "HistorialScreen - Cargando - Claro")
@Composable
private fun HistorialScreenCargandoClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        HistorialTabContent(
            menusState = State.Loading,
            displayMenus = emptyList(),
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

@Preview(showBackground = true, name = "HistorialScreen - Cargando - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HistorialScreenCargandoOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        HistorialTabContent(
            menusState = State.Loading,
            displayMenus = emptyList(),
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

@Preview(showBackground = true, name = "HistorialScreen - Error - Claro")
@Composable
private fun HistorialScreenErrorClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        HistorialTabContent(
            menusState = State.Error("Sin conexión. Verifica tu red e intenta de nuevo."),
            displayMenus = emptyList(),
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

@Preview(showBackground = true, name = "HistorialScreen - Error - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HistorialScreenErrorOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        HistorialTabContent(
            menusState = State.Error("Sin conexión. Verifica tu red e intenta de nuevo."),
            displayMenus = emptyList(),
            onLoadMenus = {}, onSearch = {}, onResetSearch = {},
            onFilterByDate = {}, onFilterByDateRange = { _, _ -> }, onClearDateFilter = {}
        )
    }
}

// MenuHistorialCard

@Preview(showBackground = true, name = "MenuHistorialCard - Claro")
@Composable
private fun MenuHistorialCardClaroPreview() {
    PreviewWrapper(darkTheme = false) { MenuHistorialCard(previewMenu) }
}

@Preview(showBackground = true, name = "MenuHistorialCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuHistorialCardOscuroPreview() {
    PreviewWrapper(darkTheme = true) { MenuHistorialCard(previewMenu) }
}

// OpcionItem

@Preview(showBackground = true, name = "OpcionItem - Claro")
@Composable
private fun OpcionItemClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        OpcionItem(icono = Icons.Filled.Visibility, texto = "Ver detalle", onClick = {})
    }
}

@Preview(showBackground = true, name = "OpcionItem - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun OpcionItemOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        OpcionItem(icono = Icons.Filled.Visibility, texto = "Ver detalle", onClick = {})
    }
}

// MenuHistorialOpcionesSheet

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MenuHistorialOpcionesSheet - Claro")
@Composable
private fun MenuHistorialOpcionesSheetClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        MenuHistorialOpcionesSheet(menu = previewMenu, onDismiss = {}, onVerCarta = {}, onEditarMenu = {}, onCompartir = {}, onEliminar = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MenuHistorialOpcionesSheet - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuHistorialOpcionesSheetOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        MenuHistorialOpcionesSheet(menu = previewMenu, onDismiss = {}, onVerCarta = {}, onEditarMenu = {}, onCompartir = {}, onEliminar = {})
    }
}

// DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DatePickerDialog - Fecha Simple - Claro")
@Composable
private fun DatePickerDialogSimpleClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        DatePickerDialog(isRangeMode = false, onRangeModeChange = {}, initialSelectedDateMillis = null, initialStartDateMillis = null, initialEndDateMillis = null, onApply = { _, _, _ -> }, onClear = {}, onDismiss = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DatePickerDialog - Fecha Simple - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DatePickerDialogSimpleOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        DatePickerDialog(isRangeMode = false, onRangeModeChange = {}, initialSelectedDateMillis = null, initialStartDateMillis = null, initialEndDateMillis = null, onApply = { _, _, _ -> }, onClear = {}, onDismiss = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DatePickerDialog - Rango - Claro")
@Composable
private fun DatePickerDialogRangoClaroPreview() {
    PreviewWrapper(darkTheme = false) {
        DatePickerDialog(isRangeMode = true, onRangeModeChange = {}, initialSelectedDateMillis = null, initialStartDateMillis = null, initialEndDateMillis = null, onApply = { _, _, _ -> }, onClear = {}, onDismiss = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DatePickerDialog - Rango - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DatePickerDialogRangoOscuroPreview() {
    PreviewWrapper(darkTheme = true) {
        DatePickerDialog(isRangeMode = true, onRangeModeChange = {}, initialSelectedDateMillis = null, initialStartDateMillis = null, initialEndDateMillis = null, onApply = { _, _, _ -> }, onClear = {}, onDismiss = {})
    }
}
