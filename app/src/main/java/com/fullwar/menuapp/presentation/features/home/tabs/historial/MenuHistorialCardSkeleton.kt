package com.fullwar.menuapp.presentation.features.home.tabs.historial

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.presentation.common.components.ShimmerBox
import com.fullwar.menuapp.ui.theme.*

/**
 * Skeleton que replica la estructura visual del composable MenuHistorialCard.
 *
 * Estructura:
 * - Imagen placeholder (60×60)
 * - Column con 4 líneas de texto placeholder (título, platos, entradas, tiempo)
 * - Separador horizontal
 */
@Composable
fun MenuHistorialCardSkeleton() {
    val imageShape = RoundedCornerShape(CornerRadiusSmall)
    val lineShape = RoundedCornerShape(CornerRadiusXSmall)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen placeholder (60×60, mismo tamaño que CustomImageView por defecto)
        ShimmerBox(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .clip(imageShape)
        )

        Spacer(modifier = Modifier.width(SpacingMedium))

        Column(modifier = Modifier.weight(1f)) {
            // Título (descripcionFecha)
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(TextSizeMedium.value.dp)
                    .clip(lineShape)
            )
            Spacer(modifier = Modifier.height(SpacingXSmall))

            // Platos resumen
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(TextSizeSmall.value.dp)
                    .clip(lineShape)
            )
            Spacer(modifier = Modifier.height(SpacingXSmall))

            // Entradas resumen
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(TextSizeSmall.value.dp)
                    .clip(lineShape)
            )
            Spacer(modifier = Modifier.height(SpacingXSmall))

            // Tiempo transcurrido
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(TextSizeXSmall.value.dp)
                    .clip(lineShape)
            )
        }
    }

    HorizontalDivider(color = HeavyGray)
}

// --- Previews ---

@Preview(showBackground = true, name = "MenuHistorialCardSkeleton - Claro")
@Composable
private fun MenuHistorialCardSkeletonClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        MenuHistorialCardSkeleton()
    }
}

@Preview(showBackground = true, name = "MenuHistorialCardSkeleton - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuHistorialCardSkeletonOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            MenuHistorialCardSkeleton()
        }
    }
}
