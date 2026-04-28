package com.fullwar.menuapp.presentation.features.menu.entrada.gestion.editar

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
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheetContent
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaForm
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.ui.theme.MenuAppTheme

@Composable
fun EditarEntradaBottomSheet(
    viewModel: EntradaViewModel,
    entrada: EntradaResponseDto,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val editState = viewModel.editState

    LaunchedEffect(entrada.id) {
        viewModel.initForEdit(entrada)
    }

    LaunchedEffect(editState) {
        if (editState is State.Success<EntradaResponseDto>) {
            onSuccess()
        }
    }

    GestionBottomSheet(
        title = stringResource(R.string.entrada_editar_titulo),
        saveLabel = stringResource(R.string.entrada_guardar_cambios),
        cancelLabel = stringResource(R.string.entrada_cancelar),
        isLoading = editState is State.Loading,
        errorMessage = (editState as? State.Error)?.message,
        onSave = { viewModel.save(context) },
        onDismiss = onDismiss
    ) {
        EntradaForm(viewModel = viewModel)
    }
}

@Preview(showBackground = true, name = "EditarEntrada - Normal - Claro")
@Composable
private fun EditarEntradaNormalClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Entrada",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}

@Preview(showBackground = true, name = "EditarEntrada - Normal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EditarEntradaNormalOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Editar Entrada",
                saveLabel = "Guardar Cambios",
                cancelLabel = "Cancelar",
                isLoading = false,
                errorMessage = null,
                onSave = {},
                onDismiss = {}
            ) {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "EditarEntrada - Cargando - Claro")
@Composable
private fun EditarEntradaCargandoClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Entrada",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = true,
            errorMessage = null,
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}

@Preview(showBackground = true, name = "EditarEntrada - Error - Claro")
@Composable
private fun EditarEntradaErrorClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Editar Entrada",
            saveLabel = "Guardar Cambios",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = "No se pudo guardar la entrada. Intenta nuevamente.",
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
