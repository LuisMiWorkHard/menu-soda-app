package com.fullwar.menuapp.presentation.features.menu.plato.gestion.editar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoFormContent
import com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared.PlatoViewModel
import com.fullwar.menuapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlatoBottomSheet(
    viewModel: PlatoViewModel,
    plato: PlatoResponseDto,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editState = viewModel.editState

    LaunchedEffect(plato.id) {
        viewModel.initForEdit(plato)
    }

    LaunchedEffect(editState) {
        if (editState is State.Success<Unit>) {
            onSuccess()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpacingXLarge)
                .padding(bottom = SpacingXXLarge)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.plato_editar_titulo),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeXLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = HeavyGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingLarge))

            PlatoFormContent(viewModel = viewModel)

            Spacer(modifier = Modifier.height(SpacingXXLarge))

            // Error general del servidor
            if (editState is State.Error) {
                Surface(
                    color = Color(0xFFFCE4EC),
                    shape = RoundedCornerShape(CornerRadiusSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpacingLarge)
                ) {
                    Text(
                        text = editState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = TextSizeSmall,
                        modifier = Modifier.padding(SpacingMedium)
                    )
                }
            }

            // Botón: Guardar Cambios
            Button(
                onClick = { viewModel.save(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightLarge),
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = editState !is State.Loading
            ) {
                if (editState is State.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(IconSizeMedium),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.plato_guardar_cambios),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMedium))

            // Botón: Cancelar
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.plato_cancelar),
                    color = HeavyGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextSizeMedium
                )
            }
        }
    }
}
