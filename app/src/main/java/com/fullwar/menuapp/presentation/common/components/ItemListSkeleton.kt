package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.*

/**
 * Skeleton que replica la estructura visual compartida por EntradaListItem y PlatoDisponibleCard.
 *
 * Estructura:
 * - Imagen placeholder (configurable, 60dp por defecto)
 * - Column con 2 líneas de texto placeholder (nombre, descripción)
 * - Checkbox placeholder
 */
@Composable
fun ItemListSkeleton(
    imageSizeDp: Int = 60
) {
    val imageShape = RoundedCornerShape(CornerRadiusSmall)
    val lineShape = RoundedCornerShape(CornerRadiusXSmall)

    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen placeholder
            ShimmerBox(
                modifier = Modifier
                    .size(imageSizeDp.dp)
                    .clip(imageShape)
            )

            Spacer(modifier = Modifier.width(SpacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                // Nombre
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(TextSizeMedium.value.dp)
                        .clip(lineShape)
                )
                Spacer(modifier = Modifier.height(SpacingSmall))

                // Descripción
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(TextSizeSmall.value.dp)
                        .clip(lineShape)
                )
            }

            // Checkbox placeholder
            ShimmerBox(
                modifier = Modifier
                    .size(20.dp)
                    .clip(lineShape)
            )
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "ItemListSkeleton - Claro")
@Composable
private fun ItemListSkeletonClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        ItemListSkeleton()
    }
}

@Preview(showBackground = true, name = "ItemListSkeleton - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ItemListSkeletonOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ItemListSkeleton()
        }
    }
}
