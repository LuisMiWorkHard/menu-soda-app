package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.fullwar.menuapp.ui.theme.CornerRadiusLarge
import com.fullwar.menuapp.ui.theme.IconSize2XLarge
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.SpacingXXLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeXLarge

@Composable
internal fun ConfirmDeleteBottomSheetContent(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingXLarge)
            .padding(bottom = SpacingXXLarge),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(IconSize2XLarge))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeXLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Text(
                    text = message,
                    fontWeight = FontWeight.Normal,
                    fontSize = TextSizeMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SpacingLarge))

        Surface(
            shape = RoundedCornerShape(CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onConfirm)
                        .padding(SpacingMedium)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(SpacingMedium))
                    Text(
                        text = confirmLabel,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        fontSize = TextSizeMedium
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDismiss)
                        .padding(SpacingMedium)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowCircleLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(SpacingMedium))
                    Text(
                        text = dismissLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                        fontSize = TextSizeMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeleteBottomSheet(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        ConfirmDeleteBottomSheetContent(
            title = title,
            message = message,
            confirmLabel = confirmLabel,
            dismissLabel = dismissLabel,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Preview(showBackground = true, name = "ConfirmDelete - Claro")
@Composable
private fun ConfirmDeleteBottomSheetClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        ConfirmDeleteBottomSheetContent(
            title = "Eliminar entrada",
            message = "¿Deseas eliminar \"Ceviche Clásico\"? Esta acción no se puede deshacer.",
            confirmLabel = "Eliminar",
            dismissLabel = "Cancelar",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "ConfirmDelete - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ConfirmDeleteBottomSheetOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ConfirmDeleteBottomSheetContent(
                title = "Eliminar entrada",
                message = "¿Deseas eliminar \"Ceviche Clásico\"? Esta acción no se puede deshacer.",
                confirmLabel = "Eliminar",
                dismissLabel = "Cancelar",
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
