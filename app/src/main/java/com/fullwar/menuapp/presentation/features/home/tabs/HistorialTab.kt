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
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.*

data class MenuHistorialItem(
    val fecha: String,
    val platoPrincipal: String,
    val entrada: String,
    val colorFondo: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialTab(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Hoy") }

    val filters = listOf("Hoy", "Última Semana", "Platos Principales")

    // Datos de ejemplo
    val menuItems = listOf(
        MenuHistorialItem(
            "Lunes, 24 Oct",
            "Principal: Pollo Rostizado, Ensalada…",
            "Entrada: Sopa de Calabaza",
            SodaOrangeLight
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
                    IconButton(onClick = { /* Calendario */ }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = SodaOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Nuevo menú */ },
                containerColor = SodaOrange,
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
                .background(Color.White)
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
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Tune,
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
                            selectedContainerColor = SodaOrange,
                            selectedLabelColor = Color.White,
                            selectedTrailingIconColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingLarge))

            // Título de sección
            Text(
                text = stringResource(id = R.string.historial_recientes),
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeSmall,
                color = SodaGray
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
                tint = SodaOrange,
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
                color = SodaGray,
                maxLines = 1
            )
            Text(
                text = item.entrada,
                fontSize = TextSizeSmall,
                color = SodaGray,
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
                    tint = SodaOrange,
                    modifier = Modifier.padding(SpacingSmall)
                )
            }
        }
    }
    HorizontalDivider(color = SodaGrayLight)
}
