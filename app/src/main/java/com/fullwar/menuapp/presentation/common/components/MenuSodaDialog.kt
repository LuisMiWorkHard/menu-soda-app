package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fullwar.menuapp.ui.theme.ButtonHeightMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusXLarge
import com.fullwar.menuapp.ui.theme.ElevationSmall
import com.fullwar.menuapp.ui.theme.IconSizeLarge
import com.fullwar.menuapp.ui.theme.ImageSizeLarge
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge

enum class MenuSodaDialogVariant { Default, Error, Warning }

@Composable
internal fun MenuSodaDialogContent(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageVector? = null,
    variant: MenuSodaDialogVariant = MenuSodaDialogVariant.Default,
) {
    val iconContainerColor: Color
    val iconTint: Color
    val confirmButtonColor: Color
    val confirmButtonContentColor: Color

    when (variant) {
        MenuSodaDialogVariant.Error -> {
            // errorContainer es rosado/rojo claro en M3 — contraste adecuado con el ícono
            iconContainerColor = MaterialTheme.colorScheme.errorContainer
            iconTint = MaterialTheme.colorScheme.error
            confirmButtonColor = MaterialTheme.colorScheme.error
            confirmButtonContentColor = MaterialTheme.colorScheme.onError
        }
        MenuSodaDialogVariant.Warning -> {
            // surfaceVariant (gris sage) en lugar de primaryContainer (lavanda M3 imprevisible)
            iconContainerColor = MaterialTheme.colorScheme.surfaceVariant
            iconTint = MaterialTheme.colorScheme.error
            confirmButtonColor = MaterialTheme.colorScheme.error
            confirmButtonContentColor = MaterialTheme.colorScheme.onError
        }
        MenuSodaDialogVariant.Default -> {
            iconContainerColor = MaterialTheme.colorScheme.surfaceVariant
            iconTint = MaterialTheme.colorScheme.primary
            confirmButtonColor = MaterialTheme.colorScheme.primary
            confirmButtonContentColor = MaterialTheme.colorScheme.onPrimary
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(CornerRadiusXLarge),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = ElevationSmall
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(ImageSizeLarge)
                        .background(iconContainerColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(IconSizeLarge)
                    )
                }
                Spacer(modifier = Modifier.height(SpacingLarge))
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeXLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            Text(
                text = message,
                fontSize = TextSizeSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(SpacingXLarge))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightMedium),
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmButtonColor,
                    contentColor = confirmButtonContentColor
                )
            ) {
                Text(text = confirmLabel, fontWeight = FontWeight.SemiBold)
            }

            if (dismissLabel != null) {
                Spacer(modifier = Modifier.height(SpacingXSmall))
                TextButton(
                    onClick = onDismiss ?: onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dismissLabel,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(SpacingSmall))
            }
        }
    }
}

@Composable
fun MenuSodaDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageVector? = null,
    variant: MenuSodaDialogVariant = MenuSodaDialogVariant.Default,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        MenuSodaDialogContent(
            title = title,
            message = message,
            onDismissRequest = onDismissRequest,
            confirmLabel = confirmLabel,
            onConfirm = onConfirm,
            modifier = modifier,
            dismissLabel = dismissLabel,
            onDismiss = onDismiss,
            icon = icon,
            variant = variant
        )
    }
}

@Preview(showBackground = true, name = "Error - Claro")
@Preview(showBackground = true, name = "Error - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewError() {
    MenuAppTheme {
        MenuSodaDialogContent(
            title = "Error al guardar",
            message = "Ocurrió un problema al intentar guardar el menú. Intenta nuevamente.",
            onDismissRequest = {},
            confirmLabel = "Aceptar",
            onConfirm = {},
            icon = Icons.Filled.ErrorOutline,
            variant = MenuSodaDialogVariant.Error
        )
    }
}

@Preview(showBackground = true, name = "Warning con dismiss - Claro")
@Preview(showBackground = true, name = "Warning con dismiss - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewWarning() {
    MenuAppTheme {
        MenuSodaDialogContent(
            title = "Eliminar menú",
            message = "¿Estás seguro de que deseas eliminar el menú del Lunes, 24 Oct? Esta acción no se puede deshacer.",
            onDismissRequest = {},
            confirmLabel = "Eliminar",
            onConfirm = {},
            dismissLabel = "Cancelar",
            onDismiss = {},
            icon = Icons.Filled.Delete,
            variant = MenuSodaDialogVariant.Warning
        )
    }
}

@Preview(showBackground = true, name = "Default info - Claro")
@Preview(showBackground = true, name = "Default info - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDefault() {
    MenuAppTheme {
        MenuSodaDialogContent(
            title = "Información",
            message = "Esta es una notificación informativa para el usuario.",
            onDismissRequest = {},
            confirmLabel = "Entendido",
            onConfirm = {}
        )
    }
}
