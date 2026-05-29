package com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.ButtonHeightLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.DangerRed
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSizeLarge
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXXLarge
import com.fullwar.menuapp.ui.theme.StrokeWidthMedium
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXXLarge
import com.fullwar.menuapp.ui.theme.TextSize3XLarge
import com.fullwar.menuapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecuperarContrasenaScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCodigoCorrecto: () -> Unit = {},
    viewModel: RecuperarContrasenaViewModel = koinViewModel()
) {
    LaunchedEffect(viewModel.verificarState) {
        if (viewModel.verificarState is State.Success) onCodigoCorrecto()
    }

    RecuperarContrasenaContent(
        modifier         = modifier,
        enviarState      = viewModel.enviarState,
        verificarState   = viewModel.verificarState,
        codigo           = viewModel.codigo,
        tiempoRestante   = viewModel.tiempoRestante,
        onEnviarCodigo   = viewModel::enviarCodigo,
        onDigitChange    = viewModel::onDigitChange,
        onReenviarCodigo = viewModel::reenviarCodigo,
        onBack           = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecuperarContrasenaContent(
    modifier: Modifier = Modifier,
    enviarState: State<String> = State.Initial,
    verificarState: State<Unit> = State.Initial,
    codigo: List<String> = listOf("", "", "", ""),
    tiempoRestante: Int = 0,
    onEnviarCodigo: () -> Unit = {},
    onDigitChange: (Int, String) -> Unit = { _, _ -> },
    onReenviarCodigo: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isEnviando   = enviarState is State.Loading
    val isVerificando = verificarState is State.Loading
    val codigoEnviado = enviarState is State.Success

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.recuperar_contrasena_titulo),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.recuperar_contrasena_volver),
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
                text = stringResource(id = R.string.recuperar_contrasena_titulo_pantalla),
                fontSize = TextSizeXXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            val emailMasked = (enviarState as? State.Success)?.data ?: ""
            Text(
                text = if (codigoEnviado)
                    stringResource(id = R.string.recuperar_contrasena_codigo_enviado_a, emailMasked)
                else
                    stringResource(id = R.string.recuperar_contrasena_descripcion),
                fontSize = TextSizeSmall,
                color = HeavyGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SpacingXXLarge))

            if (!codigoEnviado) {
                // — Etapa 1: Botón enviar código —
                Button(
                    onClick = onEnviarCodigo,
                    enabled = !isEnviando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ButtonHeightLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(CornerRadiusMedium)
                ) {
                    if (isEnviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(IconSizeMedium),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = StrokeWidthMedium
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.recuperar_contrasena_boton),
                            fontSize = TextSizeLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (enviarState is State.Error) {
                    ErrorBanner(
                        message = enviarState.message,
                        modifier = Modifier.padding(top = SpacingSmall)
                    )
                }
            } else {
                // — Etapa 2: Inputs OTP + countdown —

                // 4 campos OTP
                OtpInputRow(
                    codigo = codigo,
                    enabled = !isVerificando,
                    onDigitChange = onDigitChange
                )

                Spacer(modifier = Modifier.height(SpacingMedium))

                // Spinner de verificación
                AnimatedVisibility(visible = isVerificando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(IconSizeMedium),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = StrokeWidthMedium
                    )
                }

                // Countdown
                if (tiempoRestante > 0) {
                    val mm = tiempoRestante / 60
                    val ss = tiempoRestante % 60
                    val countdownColor = when {
                        tiempoRestante <= 30 -> DangerRed
                        tiempoRestante <= 60 -> MaterialTheme.colorScheme.error
                        else                 -> HeavyGray
                    }
                    Text(
                        text = stringResource(
                            id = R.string.recuperar_contrasena_valido_por,
                            "%02d:%02d".format(mm, ss)
                        ),
                        fontSize = TextSizeSmall,
                        color = countdownColor
                    )
                }

                if (verificarState is State.Error) {
                    ErrorBanner(
                        message = verificarState.message,
                        modifier = Modifier.padding(top = SpacingSmall)
                    )
                }

                Spacer(modifier = Modifier.height(SpacingMedium))

                // Reenviar código
                TextButton(
                    onClick = onReenviarCodigo,
                    enabled = !isEnviando
                ) {
                    if (isEnviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(IconSizeMedium),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = StrokeWidthMedium
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.recuperar_contrasena_reenviar),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = TextSizeSmall
                        )
                    }
                }
            }
        }
    }
}

// ─── Componente privado: fila de 4 inputs OTP ─────────────────────────────

@Composable
private fun OtpInputRow(
    codigo: List<String>,
    enabled: Boolean,
    onDigitChange: (Int, String) -> Unit
) {
    val focusRequesters = remember { List(4) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        codigo.forEachIndexed { index, digit ->
            OtpBox(
                value = digit,
                enabled = enabled,
                focusRequester = focusRequesters[index],
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }.take(1)
                    onDigitChange(index, filtered)
                    if (filtered.isNotEmpty() && index < 3) {
                        focusRequesters[index + 1].requestFocus()
                    }
                },
                onBackspace = {
                    if (digit.isEmpty() && index > 0) {
                        onDigitChange(index - 1, "")
                        focusRequesters[index - 1].requestFocus()
                    }
                }
            )
        }
    }
}

@Composable
private fun OtpBox(
    value: String,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val isFilled = value.isNotEmpty()
    BasicTextField(
        value = value,
        onValueChange = { newVal ->
            if (newVal.isEmpty()) onBackspace()
            else onValueChange(newVal)
        },
        enabled = enabled,
        textStyle = TextStyle(
            fontSize = TextSize3XLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        modifier = Modifier
            .size(64.dp)
            .focusRequester(focusRequester)
            .border(
                width = StrokeWidthMedium,
                color = if (isFilled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                shape = RoundedCornerShape(CornerRadiusMedium)
            )
            .background(
                color = if (isFilled) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        else White,
                shape = RoundedCornerShape(CornerRadiusMedium)
            ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                innerTextField()
            }
        }
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "RecuperarContrasena - Etapa 1 Claro")
@Preview(showBackground = true, name = "RecuperarContrasena - Etapa 1 Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RecuperarContrasenaEtapa1Preview() {
    MenuAppTheme {
        RecuperarContrasenaContent()
    }
}

@Preview(showBackground = true, name = "RecuperarContrasena - Etapa 2 Claro")
@Preview(showBackground = true, name = "RecuperarContrasena - Etapa 2 Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RecuperarContrasenaEtapa2Preview() {
    MenuAppTheme {
        RecuperarContrasenaContent(
            enviarState    = State.Success("c****o@ejemplo.com"),
            tiempoRestante = 245
        )
    }
}
