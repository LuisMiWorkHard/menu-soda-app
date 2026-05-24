package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.ButtonHeightLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusLarge
import com.fullwar.menuapp.ui.theme.ErrorSurface
import com.fullwar.menuapp.ui.theme.White
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXXLarge
import com.fullwar.menuapp.ui.theme.StrokeWidthMedium
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import com.fullwar.menuapp.ui.theme.TextSizeSmall

@Composable
internal fun GestionBottomSheetContent(
    title: String,
    saveLabel: String,
    cancelLabel: String,
    isLoading: Boolean,
    errorMessage: String?,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacingXLarge)
            .padding(bottom = SpacingXXLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeXLarge
            )
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

        content()

        Spacer(modifier = Modifier.height(SpacingXXLarge))

        if (errorMessage != null) {
            Surface(
                color = ErrorSurface,
                shape = RoundedCornerShape(CornerRadiusSmall),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SpacingLarge)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = TextSizeSmall,
                    modifier = Modifier.padding(SpacingMedium)
                )
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonHeightLarge),
            shape = RoundedCornerShape(CornerRadiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(IconSizeMedium),
                    strokeWidth = StrokeWidthMedium
                )
            } else {
                Text(
                    text = saveLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingMedium))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = cancelLabel,
                color = HeavyGray,
                fontWeight = FontWeight.Medium,
                fontSize = TextSizeMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Normal - Claro")
@Composable
private fun GestionBottomSheetNormalClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Agregar categoría",
            saveLabel = "Guardar",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Text(text = "Contenido del formulario aquí")
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Normal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GestionBottomSheetNormalOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Agregar categoría",
                saveLabel = "Guardar",
                cancelLabel = "Cancelar",
                isLoading = false,
                errorMessage = null,
                onSave = {},
                onDismiss = {}
            ) {
                Text(text = "Contenido del formulario aquí")
            }
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Cargando - Claro")
@Composable
private fun GestionBottomSheetCargandoClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Agregar categoría",
            saveLabel = "Guardar",
            cancelLabel = "Cancelar",
            isLoading = true,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Text(text = "Contenido del formulario aquí")
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Cargando - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GestionBottomSheetCargandoOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Agregar categoría",
                saveLabel = "Guardar",
                cancelLabel = "Cancelar",
                isLoading = true,
                errorMessage = null,
                onSave = {},
                onDismiss = {}
            ) {
                Text(text = "Contenido del formulario aquí")
            }
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Con Error - Claro")
@Composable
private fun GestionBottomSheetErrorClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Agregar categoría",
            saveLabel = "Guardar",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = "Ocurrió un error al guardar los cambios.",
            onSave = {},
            onDismiss = {}
        ) {
            Text(text = "Contenido del formulario aquí")
        }
    }
}

@Preview(showBackground = true, name = "GestionBottomSheet - Con Error - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GestionBottomSheetErrorOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Agregar categoría",
                saveLabel = "Guardar",
                cancelLabel = "Cancelar",
                isLoading = false,
                errorMessage = "Ocurrió un error al guardar los cambios.",
                onSave = {},
                onDismiss = {}
            ) {
                Text(text = "Contenido del formulario aquí")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBottomSheet(
    title: String,
    saveLabel: String,
    cancelLabel: String,
    isLoading: Boolean,
    errorMessage: String?,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    swipeToDismissEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value -> swipeToDismissEnabled || value != SheetValue.Hidden }
    )

    val onDismissRequest: () -> Unit = { if (swipeToDismissEnabled) onDismiss() }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        GestionBottomSheetContent(
            title = title,
            saveLabel = saveLabel,
            cancelLabel = cancelLabel,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onSave = onSave,
            onDismiss = onDismiss,
            content = content
        )
    }
}
