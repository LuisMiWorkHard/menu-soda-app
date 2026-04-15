package com.fullwar.menuapp.presentation.features.menu.plato

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto
import com.fullwar.menuapp.domain.model.TipoPlato
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.*

@Composable
fun PlatoFormContent(viewModel: PlatoViewModel) {
    val nombre = viewModel.formFields.fields["platnom"] as? TextFieldValue ?: TextFieldValue()
    val descripcion = viewModel.formFields.fields["platdes"] as? TextFieldValue ?: TextFieldValue()
    val tipoSeleccionado = viewModel.formFields.fields["codtippla"] as? TipoPlato
    val tiposState = viewModel.tiposPlatoState
    val errors: Map<String, Int?> = viewModel.formFields.errors
    val serverErrors: Map<String, String?> = viewModel.formFields.serverErrors

    val descripcionFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Campo: Nombre del plato
    Text(
        text = stringResource(R.string.plato_nombre_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))
    OutlinedTextField(
        value = nombre,
        onValueChange = { viewModel.updateField("platnom", it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.plato_nombre_placeholder),
                color = HeavyGray
            )
        },
        shape = RoundedCornerShape(CornerRadiusMedium),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { descripcionFocusRequester.requestFocus() }
        ),
        isError = errors["platnom"] != null || serverErrors["platnom"] != null,
        supportingText = {
            val errorRes = errors["platnom"]
            val serverError = serverErrors["platnom"]
            when {
                errorRes != null -> Text(stringResource(errorRes), color = MaterialTheme.colorScheme.error)
                serverError != null -> Text(serverError, color = MaterialTheme.colorScheme.error)
                else -> Text("${nombre.text.length}/100", color = HeavyGray, fontSize = TextSizeXSmall)
            }
        }
    )

    Spacer(modifier = Modifier.height(SpacingLarge))

    // Campo: Descripción del plato
    Text(
        text = stringResource(R.string.plato_descripcion_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))
    OutlinedTextField(
        value = descripcion,
        onValueChange = { viewModel.updateField("platdes", it) },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(descripcionFocusRequester),
        placeholder = {
            Text(
                text = stringResource(R.string.plato_descripcion_placeholder),
                color = HeavyGray
            )
        },
        shape = RoundedCornerShape(CornerRadiusMedium),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        minLines = 3,
        maxLines = 5,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        isError = errors["platdes"] != null || serverErrors["platdes"] != null,
        supportingText = {
            val errorRes = errors["platdes"]
            val serverError = serverErrors["platdes"]
            when {
                errorRes != null -> Text(stringResource(errorRes), color = MaterialTheme.colorScheme.error)
                serverError != null -> Text(serverError, color = MaterialTheme.colorScheme.error)
                else -> Text("${descripcion.text.length}/150", color = HeavyGray, fontSize = TextSizeXSmall)
            }
        }
    )

    Spacer(modifier = Modifier.height(SpacingLarge))

    // Tipo de plato (chips)
    Text(
        text = stringResource(R.string.plato_tipo_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))

    when (tiposState) {
        is State.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSizeMedium)
            )
        }
        is State.Success<List<TipoPlato>> -> {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(SpacingSmall),
                verticalArrangement = Arrangement.spacedBy(SpacingSmall)
            ) {
                tiposState.data.forEach { tipo ->
                    val isSelected = tipoSeleccionado?.id == tipo.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateField("codtippla", tipo) },
                        label = { Text(text = tipo.descripcion, fontSize = TextSizeSmall) },
                        shape = RoundedCornerShape(CornerRadiusMedium),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderColor = HeavyGray.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
        is State.Error -> {
            Text(
                text = tiposState.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = TextSizeSmall
            )
        }
        else -> {}
    }

    val tipoError = errors["codtippla"]
    val tipoServerError = serverErrors["codtippla"]
    if (tipoError != null) {
        Text(
            text = stringResource(tipoError),
            color = MaterialTheme.colorScheme.error,
            fontSize = TextSizeXSmall,
            modifier = Modifier.padding(top = SpacingXSmall)
        )
    } else if (tipoServerError != null) {
        Text(
            text = tipoServerError,
            color = MaterialTheme.colorScheme.error,
            fontSize = TextSizeXSmall,
            modifier = Modifier.padding(top = SpacingXSmall)
        )
    }
}

// --- Previews ---

private class FakePlatoRepository : IPlatoRepository {
    override suspend fun getPlatos(): List<PlatoResponseDto> = emptyList()
    override suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto = throw NotImplementedError()
    override suspend fun getTiposPlato(): List<TipoPlatoResponseDto> = listOf(
        TipoPlatoResponseDto(1, "Carnes", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(2, "Pescados", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(3, "Vegetariano", 1, "01/01/2024", "admin")
    )
}

@Composable
private fun PreviewWrapper(darkTheme: Boolean, content: @Composable () -> Unit) {
    MenuAppTheme(darkTheme = darkTheme) {
        if (darkTheme) {
            Surface(color = MaterialTheme.colorScheme.background) { content() }
        } else {
            content()
        }
    }
}

@Composable
private fun FormScrollWrapper(viewModel: PlatoViewModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacingXLarge)
            .padding(vertical = SpacingLarge)
    ) {
        PlatoFormContent(viewModel = viewModel)
    }
}

@Preview(showBackground = true, name = "PlatoForm - Vacío - Claro")
@Composable
private fun PlatoFormVacioPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) { vm.initForCreate() }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Vacío - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoFormVacioDarkPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) { vm.initForCreate() }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Con tipos - Claro")
@Composable
private fun PlatoFormConTiposPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposPlato()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Con tipos - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoFormConTiposDarkPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposPlato()
    }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Con errores - Claro")
@Composable
private fun PlatoFormConErroresPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposPlato()
        vm.validate()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Con errores - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoFormConErroresDarkPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposPlato()
        vm.validate()
    }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}
