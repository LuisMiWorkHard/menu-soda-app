package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.ui.theme.AccentGrey
import com.fullwar.menuapp.ui.theme.Charcoal
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.DarkShadow
import com.fullwar.menuapp.ui.theme.DeepCharcoal
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.IconSizeSmall
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.RichBlack
import com.fullwar.menuapp.ui.theme.Shadow
import com.fullwar.menuapp.ui.theme.SkyGray
import com.fullwar.menuapp.ui.theme.SoftBone
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import com.fullwar.menuapp.ui.theme.TextSizeXSmall
import com.fullwar.menuapp.ui.theme.White

data class EstiloMenu(
    val id: String,
    val nombre: String,
    val colorStart: Color,
    val colorEnd: Color,
    val textoClaro: Boolean = true
)

private val estilosDisponibles = listOf(
    EstiloMenu("madera_rustica", "Madera Rústica",  Shadow,     DeepCharcoal),
    EstiloMenu("minimalista",    "Minimalista",      SoftBone,   White,       textoClaro = false),
    EstiloMenu("pizarra_negra",  "Pizarra Negra",   RichBlack,  DarkShadow),
    EstiloMenu("mediterraneo",   "Mediterráneo",    Charcoal,   Shadow),
    EstiloMenu("elegante",       "Elegante",         AccentGrey, RichBlack),
    EstiloMenu("moderno_oscuro", "Moderno Oscuro",  SkyGray,    AccentGrey)
)

@Composable
fun PasoEstiloScreen(
    menuViewModel: MenuViewModel,
    modifier: Modifier = Modifier
) {
    var selectedEstiloId by remember { mutableStateOf("madera_rustica") }
    val selectedEstilo = estilosDisponibles.first { it.id == selectedEstiloId }
    val entradas = menuViewModel.selectedEntradas.toList()
    val platos = menuViewModel.selectedPlatosFuertes.toList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        item { Spacer(modifier = Modifier.height(SpacingSmall)) }

        item {
            Text(
                text = "Vista previa del menú",
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item { MenuPreviewCard(estilo = selectedEstilo, entradas = entradas, platos = platos) }

        item { ResumenSeleccionRow(cantidadEntradas = entradas.size, cantidadPlatos = platos.size) }

        item { Spacer(modifier = Modifier.height(SpacingSmall)) }

        item {
            Text(
                text = "Elige el fondo visual",
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        estilosDisponibles.chunked(2).forEach { fila ->
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                    fila.forEach { estilo ->
                        EstiloCard(
                            estilo = estilo,
                            isSelected = estilo.id == selectedEstiloId,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedEstiloId = it }
                        )
                    }
                    if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(SpacingLarge)) }
    }
}

@Composable
fun MenuPreviewCard(
    estilo: EstiloMenu,
    entradas: List<EntradaResponseDto>,
    platos: List<PlatoResponseDto>
) {
    val textColor = if (estilo.textoClaro) White else DeepCharcoal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(CornerRadiusMedium))
            .background(Brush.verticalGradient(listOf(estilo.colorStart, estilo.colorEnd)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingLarge)
                .border(1.dp, White.copy(alpha = 0.4f), RoundedCornerShape(CornerRadiusSmall))
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = SpacingLarge + SpacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SUGERENCIA DEL CHEF",
                fontSize = TextSizeXSmall,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(SpacingXSmall))
            Text(
                text = "Menú del Día",
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (entradas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Text(
                    text = "Entrantes: ${entradas.joinToString { it.nombre }}",
                    fontSize = TextSizeXSmall,
                    fontStyle = FontStyle.Italic,
                    color = textColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
            if (platos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Platos Principales: ${platos.joinToString { it.nombre }}",
                    fontSize = TextSizeXSmall,
                    fontStyle = FontStyle.Italic,
                    color = textColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ResumenSeleccionRow(cantidadEntradas: Int, cantidadPlatos: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(CornerRadiusMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.room_service_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSizeMedium)
            )
            Spacer(modifier = Modifier.width(SpacingSmall))
            Column {
                Text(
                    text = "Resumen de selección",
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.paso_estilo_resumen_desc, cantidadEntradas, cantidadPlatos),
                    fontSize = TextSizeSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EstiloCard(
    estilo: EstiloMenu,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(CornerRadiusMedium))
            .border(3.dp, borderColor, RoundedCornerShape(CornerRadiusMedium))
            .background(Brush.verticalGradient(listOf(estilo.colorStart, estilo.colorEnd)))
            .clickable { onClick(estilo.id) }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(RichBlack.copy(alpha = 0.45f))
                .padding(vertical = SpacingSmall),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = estilo.nombre,
                color = White,
                fontSize = TextSizeSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(SpacingSmall)
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(IconSizeSmall)
                )
            }
        }
    }
}

// --- Previews ---

private val fakeEntradas = listOf(
    EntradaResponseDto(id = 1, nombre = "Gazpacho", descripcion = "", tipoEntradaId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    EntradaResponseDto(id = 2, nombre = "Ensalada Mixta", descripcion = "", tipoEntradaId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
)

private val fakePlatos = listOf(
    PlatoResponseDto(id = 1, nombre = "Paella", descripcion = "", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    PlatoResponseDto(id = 2, nombre = "Filete de Ternera", descripcion = "", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
)

@Preview(showBackground = true, name = "PasoEstiloScreen - Claro")
@Composable
private fun PasoEstiloScreenPreview() {
    val vm = remember { MenuViewModel() }
    MenuAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PasoEstiloScreen(menuViewModel = vm)
        }
    }
}

@Preview(showBackground = true, name = "PasoEstiloScreen - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PasoEstiloScreenDarkPreview() {
    val vm = remember { MenuViewModel() }
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PasoEstiloScreen(menuViewModel = vm)
        }
    }
}

@Preview(showBackground = true, name = "MenuPreviewCard - Claro")
@Composable
private fun MenuPreviewCardPreview() {
    MenuAppTheme(darkTheme = false) {
        MenuPreviewCard(
            estilo = estilosDisponibles.first(),
            entradas = fakeEntradas,
            platos = fakePlatos
        )
    }
}

@Preview(showBackground = true, name = "MenuPreviewCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuPreviewCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            MenuPreviewCard(
                estilo = estilosDisponibles.first(),
                entradas = fakeEntradas,
                platos = fakePlatos
            )
        }
    }
}

@Preview(showBackground = true, name = "ResumenSeleccionRow - Claro")
@Composable
private fun ResumenSeleccionRowPreview() {
    MenuAppTheme(darkTheme = false) {
        ResumenSeleccionRow(cantidadEntradas = 2, cantidadPlatos = 2)
    }
}

@Preview(showBackground = true, name = "ResumenSeleccionRow - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ResumenSeleccionRowDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResumenSeleccionRow(cantidadEntradas = 2, cantidadPlatos = 2)
        }
    }
}

@Preview(showBackground = true, name = "EstiloCard - Seleccionado Claro")
@Composable
private fun EstiloCardSelectedPreview() {
    MenuAppTheme(darkTheme = false) {
        Row(modifier = Modifier.padding(SpacingMedium), horizontalArrangement = Arrangement.spacedBy(SpacingMedium)) {
            EstiloCard(estilo = estilosDisponibles[0], isSelected = true, modifier = Modifier.weight(1f)) {}
            EstiloCard(estilo = estilosDisponibles[1], isSelected = false, modifier = Modifier.weight(1f)) {}
        }
    }
}

@Preview(showBackground = true, name = "EstiloCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EstiloCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.padding(SpacingMedium), horizontalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                EstiloCard(estilo = estilosDisponibles[2], isSelected = false, modifier = Modifier.weight(1f)) {}
                EstiloCard(estilo = estilosDisponibles[3], isSelected = true, modifier = Modifier.weight(1f)) {}
            }
        }
    }
}
