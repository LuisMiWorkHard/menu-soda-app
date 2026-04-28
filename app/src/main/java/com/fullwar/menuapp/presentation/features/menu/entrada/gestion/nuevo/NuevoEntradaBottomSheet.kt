package com.fullwar.menuapp.presentation.features.menu.entrada.gestion.nuevo

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
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheetContent
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaForm
import com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared.EntradaViewModel
import com.fullwar.menuapp.ui.theme.MenuAppTheme

@Composable
fun NuevaEntradaBottomSheet(
    viewModel: EntradaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onSelectExisting: ((EntradaResponseDto) -> Unit)? = null
) {
    val context = LocalContext.current
    val createState = viewModel.createState

    LaunchedEffect(Unit) {
        viewModel.initForCreate()
    }

    LaunchedEffect(createState) {
        if (createState is State.Success<EntradaCreateResponseDto>) {
            onSuccess()
        }
    }

    GestionBottomSheet(
        title = stringResource(R.string.entrada_nueva_titulo),
        saveLabel = stringResource(R.string.entrada_guardar),
        cancelLabel = stringResource(R.string.entrada_cancelar),
        isLoading = createState is State.Loading,
        errorMessage = (createState as? State.Error)?.message,
        onSave = { viewModel.save(context) },
        onDismiss = onDismiss
    ) {
        EntradaForm(viewModel = viewModel, onSelectExisting = onSelectExisting)
    }
}

@Preview(showBackground = true, name = "NuevaEntrada - Normal - Claro")
@Composable
private fun NuevaEntradaNormalClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nueva Entrada",
            saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevaEntrada - Normal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NuevaEntradaNormalOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Añadir Nueva Entrada",
                saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevaEntrada - Cargando - Claro")
@Composable
private fun NuevaEntradaCargandoClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nueva Entrada",
            saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevaEntrada - Error - Claro")
@Composable
private fun NuevaEntradaErrorClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nueva Entrada",
            saveLabel = "Guardar y Seleccionar",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = "El nombre ya está registrado en el sistema.",
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
