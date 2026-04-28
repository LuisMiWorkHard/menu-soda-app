package com.fullwar.menuapp.presentation.features.menu.plato.gestion.editar

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoFormContent
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel

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
        onDismiss = onDismiss
    ) {
        PlatoFormContent(viewModel = viewModel)
    }
}
