package com.fullwar.menuapp.presentation.features.menu.plato.gestion.nuevo

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
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheetContent
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoFormContent
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.ui.theme.BottomSheetPlaceholderHeight
import com.fullwar.menuapp.ui.theme.MenuAppTheme

@Composable
fun NuevoPlatoBottomSheet(
    viewModel: PlatoViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onSelectExisting: ((PlatoResponseDto) -> Unit)? = null
) {
    val context = LocalContext.current
    val createState = viewModel.createState

    LaunchedEffect(Unit) {
        viewModel.initForCreate()
    }

    LaunchedEffect(createState) {
        if (createState is State.Success<PlatoCreateResponseDto>) {
            onSuccess()
        }
    }

    GestionBottomSheet(
        title = stringResource(R.string.plato_nuevo_titulo),
        saveLabel = stringResource(R.string.plato_guardar),
        cancelLabel = stringResource(R.string.plato_cancelar),
        isLoading = createState is State.Loading,
        errorMessage = (createState as? State.Error)?.message,
        onSave = { viewModel.save(context) },
        onDismiss = onDismiss,
        swipeToDismissEnabled = false
    ) {
        PlatoFormContent(viewModel = viewModel, onSelectExisting = onSelectExisting)
    }
}

@Preview(showBackground = true, name = "NuevoPlato - Normal - Claro")
@Composable
private fun NuevoPlatoNormalClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nuevo Plato",
            saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevoPlato - Normal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NuevoPlatoNormalOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            GestionBottomSheetContent(
                title = "Añadir Nuevo Plato",
                saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevoPlato - Cargando - Claro")
@Composable
private fun NuevoPlato_Cargando_ClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nuevo Plato",
            saveLabel = "Guardar y Seleccionar",
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

@Preview(showBackground = true, name = "NuevoPlato - Error - Claro")
@Composable
private fun NuevoPlato_Error_ClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GestionBottomSheetContent(
            title = "Añadir Nuevo Plato",
            saveLabel = "Guardar y Seleccionar",
            cancelLabel = "Cancelar",
            isLoading = false,
            errorMessage = "El nombre del plato ya está registrado en el sistema.",
            onSave = {},
            onDismiss = {}
        ) {
            Spacer(modifier = Modifier.height(BottomSheetPlaceholderHeight))
        }
    }
}
