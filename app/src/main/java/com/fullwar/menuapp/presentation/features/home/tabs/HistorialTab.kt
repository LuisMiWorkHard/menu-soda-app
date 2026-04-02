package com.fullwar.menuapp.presentation.features.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class MenuHistorialItem(
    val fecha: String,
    val platoPrincipal: String,
    val entrada: String,
    val colorFondo: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTab(
    modifier: Modifier = Modifier,
    onNuevoMenuClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Hoy") }

    // Estado del calendario
    var showDatePicker by remember { mutableStateOf(false) }
    var isRangeMode by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedEndDateMillis by remember { mutableStateOf<Long?>(null) }

    val filters = listOf("Hoy", "Última Semana", "Platos Principales")
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es")) }

    // Datos de ejemplo
    val menuItems = listOf(
        MenuHistorialItem(
            "Lunes, 24 Oct",
            "Principal: Pollo Rostizado, Ensalada…",
            "Entrada: Sopa de Calabaza",
            MaterialTheme.colorScheme.background
        ),
        MenuHistorialItem(
            "Domingo, 23 Oct",
            "Principal: Lomo Wellington, Puré…",
            "Entrada: Ensalada César",
            Color(0xFFE8F5E9)
        ),
        MenuHistorialItem(
            "Sábado, 22 Oct",
            "Principal: Corvina a la Plancha,…",
            "Entrada: Cóctel de Camarones",
            Color(0xFFE3F2FD)
        ),
        MenuHistorialItem(
            "Viernes, 21 Oct",
            "Principal: Pasta Carbonara, Pan…",
            "Entrada: Bruschetta",
            Color(0xFFFCE4EC)
        )
    )

    // Texto del chip de fecha activa (independiente de isRangeMode para no perder selección al cambiar de tab)
    val activeDateChipText = remember(selectedDateMillis, selectedStartDateMillis, selectedEndDateMillis) {
        if (selectedDateMillis != null) {
            dateFormat.format(Date(selectedDateMillis!!))
        } else if (selectedStartDateMillis != null && selectedEndDateMillis != null) {
            "${dateFormat.format(Date(selectedStartDateMillis!!))} - ${dateFormat.format(Date(selectedEndDateMillis!!))}"
        } else {
            null
        }
    }

    val hasDateFilter = activeDateChipText != null

    // Derivar el modo correcto al abrir el diálogo según la selección existente
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            isRangeMode = when {
                selectedStartDateMillis != null && selectedEndDateMillis != null -> true
                else -> false
            }
        }
    }

    // Dialog del calendario
    if (showDatePicker) {
        DatePickerDialog(
            isRangeMode = isRangeMode,
            onRangeModeChange = { isRangeMode = it },
            initialSelectedDateMillis = selectedDateMillis,
            initialStartDateMillis = selectedStartDateMillis,
            initialEndDateMillis = selectedEndDateMillis,
            onApply = { singleDate, startDate, endDate ->
                if (isRangeMode) {
                    selectedStartDateMillis = startDate
                    selectedEndDateMillis = endDate
                    selectedDateMillis = null
                } else {
                    selectedDateMillis = singleDate
                    selectedStartDateMillis = null
                    selectedEndDateMillis = null
                }
                showDatePicker = false
            },
            onClear = {
                selectedDateMillis = null
                selectedStartDateMillis = null
                selectedEndDateMillis = null
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
                        fontSize = TextSizeXLarge
                    )
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = if (hasDateFilter) MaterialTheme.colorScheme.primary else LigthGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoMenuClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(CornerRadiusLarge)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(
                    text = stringResource(id = R.string.historial_nuevo_menu),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = SpacingLarge)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.historial_buscar),
                        color = LigthGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = LigthGray
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = LigthGray
                    )
                },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = SodaGrayLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(SpacingMedium))

            // Filtros (Chips)
            Row(horizontalArrangement = Arrangement.spacedBy(SpacingSmall)) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
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
                            selectedLabelColor = Color.White,
                            selectedTrailingIconColor = Color.White
                        )
                    )
                }
            }

            // Chip de fecha activa
            if (activeDateChipText != null) {
                Spacer(modifier = Modifier.height(SpacingSmall))
                FilterChip(
                    selected = true,
                    onClick = { showDatePicker = true },
                    label = { Text(text = activeDateChipText) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                selectedDateMillis = null
                                selectedStartDateMillis = null
                                selectedEndDateMillis = null
                            },
                            modifier = Modifier.size(IconSizeSmall)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier.size(IconSizeSmall)
                            )
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

            // Título de sección
            Text(
                text = stringResource(id = R.string.historial_recientes),
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeSmall,
                color = LigthGray
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            // Lista de menús
            LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                items(menuItems) { item ->
                    MenuHistorialCard(item)
                }
                // Espacio extra para que el FAB no tape el último elemento
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
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
        // Obtener la fecha de hoy en zona horaria local
        val localCalendar = Calendar.getInstance()
        val year = localCalendar.get(Calendar.YEAR)
        val month = localCalendar.get(Calendar.MONTH)
        val day = localCalendar.get(Calendar.DAY_OF_MONTH)
        // Convertir esa fecha a medianoche UTC (formato que usa DatePicker)
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.clear()
        utcCalendar.set(year, month, day, 0, 0, 0)
        utcCalendar.timeInMillis
    }

    val noFutureDates = remember(todayUtcMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayUtcMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                return year <= currentYear
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = noFutureDates
    )

    // Clave para forzar recreación del estado del rango al limpiar
    var rangeClearKey by remember { mutableIntStateOf(0) }

    val dateRangePickerState = key(rangeClearKey) {
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialStartDateMillis,
            initialSelectedEndDateMillis = initialEndDateMillis,
            selectableDates = noFutureDates
        )
    }

    // Formato para mostrar la selección
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es")) }

    // Texto de selección en tiempo real
    val selectionText = if (isRangeMode) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis
        when {
            start != null && end != null ->
                "${dateFormat.format(Date(start))} - ${dateFormat.format(Date(end))}"
            start != null ->
                "${dateFormat.format(Date(start))} - --/--/----"
            else -> "--/--/---- - --/--/----"
        }
    } else {
        val selected = datePickerState.selectedDateMillis
        if (selected != null) dateFormat.format(Date(selected)) else "--/--/----"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Título + botón cerrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = SpacingLarge,
                            end = SpacingSmall,
                            top = SpacingMedium
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.calendar_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeXLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = LigthGray
                        )
                    }
                }

                // Sección de selección: toggle + texto de fecha
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpacingLarge),
                    shape = RoundedCornerShape(CornerRadiusMedium),
                    color = SodaGrayLight
                ) {
                    Column(
                        modifier = Modifier.padding(SpacingMedium)
                    ) {
                        // Toggle Fecha / Rango
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SegmentedButton(
                                selected = !isRangeMode,
                                onClick = { onRangeModeChange(false) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = Color.White
                                )
                            ) {
                                Text(text = stringResource(id = R.string.calendar_single_date))
                            }
                            SegmentedButton(
                                selected = isRangeMode,
                                onClick = { onRangeModeChange(true) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = Color.White
                                )
                            ) {
                                Text(text = stringResource(id = R.string.calendar_date_range))
                            }
                        }

                        Spacer(modifier = Modifier.height(SpacingSmall))

                        // Texto de selección con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = SpacingSmall)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(SpacingSmall))
                            Text(
                                text = stringResource(id = R.string.calendar_selection_label),
                                fontSize = TextSizeSmall,
                                color = LigthGray
                            )
                            Spacer(modifier = Modifier.width(SpacingXSmall))
                            Text(
                                text = selectionText,
                                fontSize = TextSizeSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpacingSmall))

                HorizontalDivider(
                    color = SodaGrayLight,
                    modifier = Modifier.padding(horizontal = SpacingLarge)
                )

                // DatePicker o DateRangePicker
                if (isRangeMode) {
                    DateRangePicker(
                        state = dateRangePickerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            todayDateBorderColor = MaterialTheme.colorScheme.primary,
                            dayInSelectionRangeContainerColor = SodaOrangeLight
                        )
                    )
                } else {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = SpacingSmall),
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            todayDateBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Determinar si hay selección activa
                val hasSelection = if (isRangeMode) {
                    dateRangePickerState.selectedStartDateMillis != null
                } else {
                    datePickerState.selectedDateMillis != null
                }

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpacingLarge),
                    horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
                ) {
                    OutlinedButton(
                        onClick = if (hasSelection) {
                            {
                                // Resetear estados internos de los pickers
                                datePickerState.selectedDateMillis = null
                                rangeClearKey++
                                onClear()
                            }
                        } else onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(CornerRadiusMedium)
                    ) {
                        Text(
                            text = stringResource(
                                id = if (hasSelection) R.string.calendar_clear else R.string.calendar_cancel
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            if (isRangeMode) {
                                onApply(null, dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis)
                            } else {
                                onApply(datePickerState.selectedDateMillis, null, null)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(CornerRadiusMedium),
                        enabled = if (isRangeMode) {
                            dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
                        } else {
                            datePickerState.selectedDateMillis != null
                        }
                    ) {
                        Text(text = stringResource(id = R.string.calendar_apply))
                    }
                }
            }
        }
    }
}

@Composable
fun MenuHistorialCard(item: MenuHistorialItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen placeholder del menú
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(CornerRadiusMedium))
                .background(item.colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSizeLarge)
            )
        }

        Spacer(modifier = Modifier.width(SpacingMedium))

        // Información del menú
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.fecha,
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeMedium
            )
            Text(
                text = item.platoPrincipal,
                fontSize = TextSizeSmall,
                color = LigthGray,
                maxLines = 1
            )
            Text(
                text = item.entrada,
                fontSize = TextSizeSmall,
                color = LigthGray,
                maxLines = 1
            )
        }

        // Botón compartir
        IconButton(onClick = { /* Compartir */ }) {
            Surface(
                shape = CircleShape,
                color = SodaOrangeLight,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(SpacingSmall)
                )
            }
        }
    }
    HorizontalDivider(color = SodaGrayLight)
}
