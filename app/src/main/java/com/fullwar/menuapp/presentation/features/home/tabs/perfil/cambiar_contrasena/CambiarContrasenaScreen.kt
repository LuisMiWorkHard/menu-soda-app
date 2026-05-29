package com.fullwar.menuapp.presentation.features.home.tabs.perfil.cambiar_contrasena

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.ButtonHeightLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.DangerRed
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSizeLarge
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.IconSizeSmall
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SoftGreen
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.SpacingXXLarge
import com.fullwar.menuapp.ui.theme.StrokeWidthMedium
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXSmall
import com.fullwar.menuapp.ui.theme.TextSizeXXLarge
import com.fullwar.menuapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun CambiarContrasenaScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onExito: () -> Unit = {},
    onOlvidaste: () -> Unit = {},
    viewModel: CambiarContrasenaViewModel = koinViewModel()
) {
    LaunchedEffect(viewModel.cambiarState) {
        if (viewModel.cambiarState is State.Success) {
            onExito()
        }
    }

    CambiarContrasenaContent(
        modifier = modifier,
        formFields = viewModel.formFields,
        cambiarState = viewModel.cambiarState,
        onFieldChange = viewModel::updateField,
        onCambiar = viewModel::cambiarContrasena,
        onBack = onBack,
        onOlvidaste = onOlvidaste
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CambiarContrasenaContent(
    modifier: Modifier = Modifier,
    formFields: DynamicFormState = DynamicFormState(),
    cambiarState: State<Unit> = State.Initial,
    onFieldChange: (String, Any?) -> Unit = { _, _ -> },
    onCambiar: () -> Unit = {},
    onBack: () -> Unit = {},
    onOlvidaste: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val nuevaFocusRequester = remember { FocusRequester() }
    val confirmarFocusRequester = remember { FocusRequester() }

    var actualVisible by remember { mutableStateOf(false) }
    var nuevaVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    val actualText = (formFields.fields["contrasenaActual"] as? TextFieldValue)?.text ?: ""
    val nuevaText  = (formFields.fields["contrasenaNueva"]  as? TextFieldValue)?.text ?: ""
    val confirmarText = (formFields.fields["confirmarContrasena"] as? TextFieldValue)?.text ?: ""

    val isLoading = cambiarState is State.Loading

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cambiar_contrasena_volver),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpacingLarge)
                .padding(bottom = SpacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(SpacingXLarge))

            // — Cabecera —
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeLarge)
                )
            }

            Spacer(modifier = Modifier.height(SpacingMedium))

            Text(
                text = stringResource(id = R.string.cambiar_contrasena_titulo),
                fontSize = TextSizeXXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            Text(
                text = stringResource(id = R.string.cambiar_contrasena_subtitulo),
                fontSize = TextSizeSmall,
                color = HeavyGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SpacingXXLarge))

            // — Contraseña actual —
            ContrasenaField(
                label = stringResource(id = R.string.cambiar_contrasena_actual),
                value = actualText,
                onValueChange = { onFieldChange("contrasenaActual", TextFieldValue(it)) },
                visible = actualVisible,
                onToggleVisible = { actualVisible = !actualVisible },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { nuevaFocusRequester.requestFocus() }
                ),
                error = formFields.errors["contrasenaActual"],
                serverError = formFields.serverErrors["contrasenaActual"]
            )

            Spacer(modifier = Modifier.height(SpacingMedium))

            // — Nueva contraseña —
            ContrasenaField(
                label = stringResource(id = R.string.cambiar_contrasena_nueva),
                value = nuevaText,
                onValueChange = { onFieldChange("contrasenaNueva", TextFieldValue(it)) },
                visible = nuevaVisible,
                onToggleVisible = { nuevaVisible = !nuevaVisible },
                fieldModifier = Modifier.focusRequester(nuevaFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { confirmarFocusRequester.requestFocus() }
                ),
                error = formFields.errors["contrasenaNueva"],
                serverError = formFields.serverErrors["contrasenaNueva"]
            )

            // Indicador de fuerza (debajo del campo nueva contraseña)
            IndicadorFuerza(contrasena = nuevaText)

            Spacer(modifier = Modifier.height(SpacingMedium))

            // — Confirmar nueva contraseña —
            ContrasenaField(
                label = stringResource(id = R.string.cambiar_contrasena_confirmar),
                value = confirmarText,
                onValueChange = { onFieldChange("confirmarContrasena", TextFieldValue(it)) },
                visible = confirmarVisible,
                onToggleVisible = { confirmarVisible = !confirmarVisible },
                fieldModifier = Modifier.focusRequester(confirmarFocusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                error = formFields.errors["confirmarContrasena"],
                serverError = formFields.serverErrors["confirmarContrasena"]
            )

            Spacer(modifier = Modifier.height(SpacingLarge))

            // — Checklist de requisitos —
            RequisitosContrasena(contrasena = nuevaText)

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // — Botón principal —
            Button(
                onClick = onCambiar,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(CornerRadiusMedium)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(IconSizeMedium),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = StrokeWidthMedium
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.cambiar_contrasena_boton),
                        fontSize = TextSizeLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (cambiarState is State.Error) {
                ErrorBanner(
                    message = cambiarState.message,
                    modifier = Modifier.padding(top = SpacingSmall)
                )
            }

            Spacer(modifier = Modifier.height(SpacingSmall))

            // — ¿Olvidaste tu contraseña? —
            TextButton(onClick = onOlvidaste) {
                Text(
                    text = stringResource(id = R.string.cambiar_contrasena_olvidaste),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = TextSizeSmall
                )
            }
        }
    }
}

// ─── Componente privado: campo de contraseña ───────────────────────────────

@Composable
private fun ContrasenaField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    modifier: Modifier = Modifier,
    fieldModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    error: Int? = null,
    serverError: String? = null
) {
    val hasError = error != null || serverError != null

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = TextSizeMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(SpacingSmall))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.take(255)) },
            modifier = fieldModifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(CornerRadiusMedium)),
            placeholder = { Text(text = "••••••••", color = HeavyGray) },
            shape = RoundedCornerShape(CornerRadiusMedium),
            trailingIcon = {
                IconButton(onClick = onToggleVisible) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = HeavyGray
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            isError = hasError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
        if (serverError != null) {
            Text(
                text = serverError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = SpacingSmall, top = SpacingXSmall)
            )
        } else if (error != null) {
            Text(
                text = stringResource(id = error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = SpacingSmall, top = SpacingXSmall)
            )
        }
    }
}

// ─── Componente privado: indicador de fuerza ──────────────────────────────

@Composable
private fun IndicadorFuerza(contrasena: String) {
    if (contrasena.isEmpty()) return

    val criterios = calcularCriterios(contrasena)
    val cumplidos = criterios.count { it }

    val (color, textoRes) = when {
        cumplidos <= 2 -> DangerRed to R.string.cambiar_contrasena_debil
        cumplidos == 3 -> Color(0xFFFF9800) to R.string.cambiar_contrasena_moderada
        else           -> SoftGreen to R.string.cambiar_contrasena_fuerte
    }
    val fraccion = when {
        cumplidos <= 2 -> 0.33f
        cumplidos == 3 -> 0.66f
        else           -> 1f
    }

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "IndicadorFuerza"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SpacingSmall)
    ) {
        LinearProgressIndicator(
            progress = { fraccion },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = animatedColor,
            trackColor = MaterialTheme.colorScheme.surface
        )
        Spacer(modifier = Modifier.height(SpacingXSmall))
        Text(
            text = stringResource(id = R.string.cambiar_contrasena_seguridad, stringResource(id = textoRes)),
            fontSize = TextSizeXSmall,
            color = animatedColor
        )
    }
}

// ─── Componente privado: checklist de requisitos ──────────────────────────

@Composable
private fun RequisitosContrasena(contrasena: String) {
    val criterios = calcularCriterios(contrasena)
    val etiquetas = listOf(
        R.string.cambiar_contrasena_req_longitud,
        R.string.cambiar_contrasena_req_mayuscula,
        R.string.cambiar_contrasena_req_minuscula,
        R.string.cambiar_contrasena_req_numero,
        R.string.cambiar_contrasena_req_especial
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingXSmall)
    ) {
        criterios.zip(etiquetas).forEach { (cumplido, etiquetaRes) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (cumplido) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                    contentDescription = null,
                    tint = if (cumplido) SoftGreen else HeavyGray,
                    modifier = Modifier.size(IconSizeSmall)
                )
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(
                    text = stringResource(id = etiquetaRes),
                    fontSize = TextSizeSmall,
                    color = if (cumplido) MaterialTheme.colorScheme.onBackground else HeavyGray
                )
            }
        }
    }
}

// ─── Función auxiliar ─────────────────────────────────────────────────────

private fun calcularCriterios(contrasena: String): List<Boolean> = listOf(
    contrasena.length >= 8,
    contrasena.any { it.isUpperCase() },
    contrasena.any { it.isLowerCase() },
    contrasena.any { it.isDigit() },
    contrasena.any { !it.isLetterOrDigit() }
)

// ─── Previews ─────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "CambiarContrasena - Claro")
@Preview(showBackground = true, name = "CambiarContrasena - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CambiarContrasenaPreview() {
    MenuAppTheme {
        CambiarContrasenaContent()
    }
}

@Preview(showBackground = true, name = "CambiarContrasena - Cargando")
@Composable
private fun CambiarContrasenaLoadingPreview() {
    MenuAppTheme {
        CambiarContrasenaContent(
            formFields = DynamicFormState(
                fields = mapOf(
                    "contrasenaActual"    to TextFieldValue("miContrasena1!"),
                    "contrasenaNueva"     to TextFieldValue("NuevaPass1!"),
                    "confirmarContrasena" to TextFieldValue("NuevaPass1!")
                )
            ),
            cambiarState = State.Loading
        )
    }
}
