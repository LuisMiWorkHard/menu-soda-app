package com.fullwar.menuapp.presentation.features.menu.estilo

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.presentation.common.components.ShimmerBox
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingMedium

/**
 * Skeleton que replica la estructura visual del grid de ImagenFondoCard.
 *
 * Muestra una fila con 2 placeholders de 140dp de alto,
 * replicando la disposición del grid de imágenes de fondo.
 */
@Composable
fun ImagenFondoCardSkeletonRow() {
    val cardShape = RoundedCornerShape(CornerRadiusMedium)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        ShimmerBox(
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
                .clip(cardShape)
        )
        ShimmerBox(
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
                .clip(cardShape)
        )
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "ImagenFondoCardSkeletonRow - Claro")
@Composable
private fun ImagenFondoCardSkeletonRowClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        ImagenFondoCardSkeletonRow()
    }
}

@Preview(showBackground = true, name = "ImagenFondoCardSkeletonRow - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ImagenFondoCardSkeletonRowOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ImagenFondoCardSkeletonRow()
        }
    }
}
