package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.ui.theme.*

data class SugerenciaPlatoItem(
    val nombre: String,
    val descripcion: String
)

data class PlatoDisponibleItem(
    val nombre: String,
    val descripcion: String
)

@Composable
fun PasoPlatosFondoScreen(menuViewModel: MenuViewModel) {
    val selectedPlatos = menuViewModel.selectedPlatosFuertes
    var searchQuery by remember { mutableStateOf("") }

    val sugerenciasMock = listOf(
        SugerenciaPlatoItem("Lomo Saltado", "Ideal con la Sopa del día."),
        SugerenciaPlatoItem("Ceviche", "Opción fresca para hoy."),
        SugerenciaPlatoItem("Ají de Gallina", "Clásico y contundente."),
        SugerenciaPlatoItem("Tallarines Verdes", "Acompañado de apanado."),
        SugerenciaPlatoItem("Arroz con Mariscos", "Sabor a mar del día.")
    )

    // Las sugerencias cambiarán cada vez que selectedPlatos cambie, con un máximo de 3.
    val sugerencias = remember(selectedPlatos) {
        val startIndex = (selectedPlatos.size * 2) % sugerenciasMock.size
        val end = minOf(startIndex + 3, sugerenciasMock.size)
        val selected = sugerenciasMock.subList(startIndex, end)
        if (selected.size < 3) {
            (selected + sugerenciasMock.take(3 - selected.size))
        } else {
            selected
        }
    }

    val platosDisponibles = listOf(
        PlatoDisponibleItem("Pollo a la Plancha", "Con ensalada fresca o papas"),
        PlatoDisponibleItem("Salmón al Horno", "Acompañado de espárragos"),
        PlatoDisponibleItem("Seco de Res", "Clásico con frijoles y arroz"),
        PlatoDisponibleItem("Pasta Pomodoro", "Opción vegetariana del día"),
        PlatoDisponibleItem("Milanesa de Pollo", "Con puré o papas fritas")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        // Buscador
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(id = R.string.platos_fondo_buscar), color = HeavyGray) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = HeavyGray) },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = HeavyGray
                ),
                singleLine = true
            )
        }

        // Sugerencias Inteligentes
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(CornerRadiusMedium),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(SpacingMedium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = YellowIdea, modifier = Modifier.size(IconSizeSmall))
                        Spacer(modifier = Modifier.width(SpacingSmall))
                        Text(text = stringResource(id = R.string.platos_fondo_sugerencias), fontWeight = FontWeight.Bold, fontSize = TextSizeSmall)
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

        // Platos Disponibles
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SpacingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.platos_fondo_disponibles), fontWeight = FontWeight.Bold, fontSize = TextSizeLarge)
                Row(modifier = Modifier.clickable { /* Nuevo plato */ }, verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(IconSizeSmall))
                    Spacer(modifier = Modifier.width(SpacingXSmall))
                    Text(text = stringResource(id = R.string.platos_fondo_nuevo_plato), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Lista de platos
        items(platosDisponibles) { plato ->
            PlatoDisponibleCard(
                item = plato,
                isSelected = plato.nombre in selectedPlatos,
                onToggle = { isChecked ->
                    menuViewModel.updatePlatosFuertes(
                        if (isChecked) selectedPlatos + plato.nombre else selectedPlatos - plato.nombre
                    )
                }
            )
        }
        item { Spacer(modifier = Modifier.height(SpacingLarge)) }
    }
}

@Composable
fun SugerenciaPlatoCard(item: SugerenciaPlatoItem, onAdd: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .width(280.dp)
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = item.descripcion, fontSize = TextSizeSmall, color = MaterialTheme.colorScheme.onSecondary)
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
            // Placeholder for image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(CornerRadiusSmall))
                    .background(HeavyGray)
            )
        }
    }
}

@Composable
fun PlatoDisponibleCard(item: PlatoDisponibleItem, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(CornerRadiusSmall))
                    .background(HeavyGray)
            )
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.nombre, fontWeight = FontWeight.Bold, fontSize = TextSizeMedium)
                Text(text = item.descripcion, fontSize = TextSizeSmall, color = HeavyGray)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = null, // Handled by Row click
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = HeavyGray
                )
            )
        }
    }
}
