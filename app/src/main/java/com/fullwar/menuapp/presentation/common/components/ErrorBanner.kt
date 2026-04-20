package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.TextSizeSmall

@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingSmall)
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(IconSizeMedium)
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = TextSizeSmall,
                modifier = Modifier.weight(1f)
            )
            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(
                        text = "Reintentar",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = TextSizeSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ErrorBanner - Sin retry - Claro")
@Composable
private fun ErrorBannerPreview() {
    MenuAppTheme(darkTheme = false) {
        ErrorBanner(
            message = "No se pudo conectar con el servidor. Verifica tu conexión.",
            modifier = Modifier.padding(SpacingMedium)
        )
    }
}

@Preview(showBackground = true, name = "ErrorBanner - Sin retry - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ErrorBannerDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ErrorBanner(
                message = "No se pudo conectar con el servidor. Verifica tu conexión.",
                modifier = Modifier.padding(SpacingMedium)
            )
        }
    }
}

@Preview(showBackground = true, name = "ErrorBanner - Con retry - Claro")
@Composable
private fun ErrorBannerWithRetryPreview() {
    MenuAppTheme(darkTheme = false) {
        ErrorBanner(
            message = "Error al cargar los datos.",
            onRetry = {},
            modifier = Modifier.padding(SpacingMedium)
        )
    }
}

@Preview(showBackground = true, name = "ErrorBanner - Con retry - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ErrorBannerWithRetryDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ErrorBanner(
                message = "Error al cargar los datos.",
                onRetry = {},
                modifier = Modifier.padding(SpacingMedium)
            )
        }
    }
}
