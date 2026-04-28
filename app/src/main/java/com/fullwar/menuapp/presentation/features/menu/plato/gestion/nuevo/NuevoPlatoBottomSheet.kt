package com.fullwar.menuapp.presentation.features.menu.plato.gestion.nuevo

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.components.GestionBottomSheet
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoFormContent
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel

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
        onDismiss = onDismiss
    ) {
        PlatoFormContent(viewModel = viewModel, onSelectExisting = onSelectExisting)
    }
}
