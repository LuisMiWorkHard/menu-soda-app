package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.*

data class SugerenciaItem(
    val nombre: String,
    val descripcion: String,
    val icon: ImageVector
)

@Composable
fun PasoEntradasScreen(
    selectedEntradas: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

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

    val entradas = remember {
        listOf(
            "Ensalada César con crutones",
            "Sopa de tomate rostizado",
            "Carpaccio de res con parmesano",
            "Brochetas de champiñones al ajillo",
            "Tostadas de atún marinado"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
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

        // Sugerencias inteligentes - título
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
        }

        // Cards de sugerencias
        items(sugerencias) { sugerencia ->
            SugerenciaCard(sugerencia = sugerencia)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Añadir nueva entrada */ }
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
                }
            }
        }

        // Lista de entradas con checkboxes
        items(entradas) { entrada ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newSelection = if (entrada in selectedEntradas) {
                                selectedEntradas - entrada
                            } else {
                                selectedEntradas + entrada
                            }
                            onSelectionChange(newSelection)
                        }
                        .padding(vertical = SpacingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = entrada in selectedEntradas,
                        onCheckedChange = { checked ->
                            val newSelection = if (checked) {
                                selectedEntradas + entrada
                            } else {
                                selectedEntradas - entrada
                            }
                            onSelectionChange(newSelection)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SodaOrange,
                            uncheckedColor = SodaGray
                        )
                    )
                    Spacer(modifier = Modifier.width(SpacingSmall))
                    Text(
                        text = entrada,
                        fontSize = TextSizeMedium
                    )
                }
                HorizontalDivider(color = SodaGrayLight)
            }
        }

        // Espacio extra para que el botón inferior no tape
        item { Spacer(modifier = Modifier.height(SpacingLarge)) }
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
