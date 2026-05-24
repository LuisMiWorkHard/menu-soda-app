package com.fullwar.menuapp.presentation.features.menu.plato.gestion.editar

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheetContent
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoFormContent
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.ui.theme.BottomSheetPlaceholderHeight
import com.fullwar.menuapp.ui.theme.MenuAppTheme

@Composable
fun EditarPlatoBottomSheet(
    viewModel: PlatoViewModel,
    plato: PlatoResponseDto,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val editState = viewModel.editState

    LaunchedEffect(plato.id) {
        viewModel.initForEdit(plato)
    }

    LaunchedEffect(editState) {
        if (editState is State.Success<Unit>) {
            onSuccess()
        }
    }

    GestionBottomSheet(
        title = stringResource(R.string.plato_editar_titulo),
        saveLabel = stringResource(R.string.plato_guardar_cambios),
        cancelLabel = stringResource(R.string.plato_cancelar),
        isLoading = editState is State.Loading,
        errorMessage = (editState as? State.Error)?.message,
        onSave = { viewModel.save(context) },
        onDismiss = onDismiss,
        swipeToDismissEnabled = false
    ) {
        PlatoFormContent(viewModel = viewModel)
    }
}

@Preview(showBackground = true, name = "EditarPlato - Normal - Claro")
@Composable
private fun EditarPlatoNormalClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Plato",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(BottomSheetPlaceholderHeight))
        }
    }
}

@Preview(showBackground = true, name = "EditarPlato - Normal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EditarPlatoNormalOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Editar Plato",
                saveLabel = "Guardar Cambios",
                cancelLabel = "Cancelar",
                isLoading = false,
                errorMessage = null,
                onSave = {},
                onDismiss = {}
            ) {
                Spacer(modifier = Modifier.height(BottomSheetPlaceholderHeight))
            }
        }
    }
}

@Preview(showBackground = true, name = "EditarPlato - Cargando - Claro")
@Composable
private fun EditarPlatoCargandoClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Plato",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = true,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(BottomSheetPlaceholderHeight))
        }
    }
}

@Preview(showBackground = true, name = "EditarPlato - Error - Claro")
@Composable
private fun EditarPlatoErrorClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Plato",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = "No se pudieron guardar los cambios. Intenta nuevamente.",
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(BottomSheetPlaceholderHeight))
        }
    }
}
