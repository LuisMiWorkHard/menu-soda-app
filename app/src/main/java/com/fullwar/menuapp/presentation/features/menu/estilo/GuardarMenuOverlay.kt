package com.fullwar.menuapp.presentation.features.menu.estilo

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.IconSize3XLarge
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeXXLarge
import com.fullwar.menuapp.ui.theme.White
import com.fullwar.menuapp.ui.theme.WhatsAppGreen
import java.io.File
import kotlin.math.sqrt

private const val AUTO_REDIRECT_MS = 30_000L
private const val AUTO_REDIRECT_SEGUNDOS = (AUTO_REDIRECT_MS / 1000).toInt()

@Composable
fun GuardarMenuOverlay(
    saveState: SaveUiState,
    onCompartir: (File) -> Unit,
    onMenuGuardado: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        GuardarMenuOverlayContent(
            saveState = saveState,
            onCompartir = onCompartir,
            onMenuGuardado = onMenuGuardado
        )
    }
}

@Composable
fun GuardarMenuOverlayContent(
    saveState: SaveUiState,
    onCompartir: (File) -> Unit,
    onMenuGuardado: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val fondoColor = if (isDarkTheme) {
        Color.Black.copy(alpha = 0.55f)
    } else {
        Color.White.copy(alpha = 0.85f)
    }
    val textoColor = if (isDarkTheme) White else Color(0xFF1A1A1A)

    val isSuccess = saveState is SaveUiState.Success
    val revealRadius = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }
    val cuentaRegresiva = remember { Animatable(1f) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            revealRadius.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
            cuentaRegresiva.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = AUTO_REDIRECT_MS.toInt(),
                    easing = LinearEasing
                )
            )
            onMenuGuardado()
        }
    }

    val showContent = revealRadius.value > 0.85f

    LaunchedEffect(showContent) {
        if (showContent) {
            checkProgress.animateTo(1f, tween(450, easing = LinearOutSlowInEasing))
        }
    }

    val segundosRestantes = (cuentaRegresiva.value * AUTO_REDIRECT_SEGUNDOS).toInt() + 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoColor),
        contentAlignment = Alignment.Center
    ) {
        if (!showContent) {
            LoadingContent(textoColor = textoColor)
        }

        if (isSuccess) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxRadius = sqrt(
                    (size.width / 2f) * (size.width / 2f) +
                        (size.height / 2f) * (size.height / 2f)
                )
                drawCircle(
                    color = WhatsAppGreen,
                    radius = revealRadius.value * maxRadius,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            }
        }

        if (showContent) {
            SuccessContent(
                imagenFile = (saveState as? SaveUiState.Success)?.imagenFile,
                checkProgress = checkProgress.value,
                progresoRestante = cuentaRegresiva.value,
                segundosRestantes = segundosRestantes,
                onMenuGuardado = onMenuGuardado,
                onCompartir = onCompartir
            )
        }
    }
}

@Composable
private fun LoadingContent(textoColor: Color) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.cooking_pot)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(SpacingLarge))
        Text(
            text = "Guardando menú...",
            color = textoColor.copy(alpha = 0.85f),
            fontSize = TextSizeMedium
        )
    }
}

@Composable
private fun SuccessContent(
    imagenFile: File?,
    checkProgress: Float,
    progresoRestante: Float,
    segundosRestantes: Int,
    onMenuGuardado: () -> Unit,
    onCompartir: (File) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(IconSize3XLarge)) {
            val strokeWidth = 5.dp.toPx()
            val r = size.minDimension / 2f - strokeWidth / 2f

            drawCircle(color = White.copy(alpha = 0.2f), radius = r)
            drawCircle(color = White, radius = r, style = Stroke(width = strokeWidth))

            val path = Path().apply {
                moveTo(size.width * 0.25f, size.height * 0.52f)
                lineTo(size.width * 0.44f, size.height * 0.70f)
                lineTo(size.width * 0.76f, size.height * 0.30f)
            }
            val measure = PathMeasure()
            measure.setPath(path, false)
            val partial = Path()
            measure.getSegment(0f, measure.length * checkProgress, partial, true)
            drawPath(
                path = partial,
                color = White,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        Spacer(Modifier.height(SpacingXLarge))

        AnimatedVisibility(
            visible = checkProgress > 0.1f,
            enter = slideInVertically(tween(350)) { 40 } + fadeIn(tween(350))
        ) {
            Text(
                text = "¡Menú guardado!",
                fontSize = TextSizeXXLarge,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(SpacingMedium))

        AnimatedVisibility(
            visible = checkProgress > 0.5f,
            enter = slideInVertically(tween(350, delayMillis = 80)) { 40 } +
                fadeIn(tween(350, delayMillis = 80))
        ) {
            Text(
                text = "El menú del día fue registrado correctamente",
                fontSize = TextSizeMedium,
                color = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(SpacingXLarge))

        AnimatedVisibility(
            visible = checkProgress > 0.9f,
            enter = fadeIn(tween(300, delayMillis = 180))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CerrarButton(
                    progresoRestante = progresoRestante,
                    segundosRestantes = segundosRestantes,
                    onClick = onMenuGuardado,
                    modifier = Modifier.weight(1f)
                )
                if (imagenFile != null) {
                    Button(
                        onClick = { onCompartir(imagenFile) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = WhatsAppGreen
                        ),
                        shape = RoundedCornerShape(CornerRadiusMedium)
                    ) {
                        Text("Compartir", fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.Share, modifier = Modifier.padding(SpacingSmall,0.dp,0.dp,0.dp), contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun CerrarButton(
    progresoRestante: Float,
    segundosRestantes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(CornerRadiusMedium)

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, White.copy(alpha = 0.7f), shape)
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp, max = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Capa 1 – fondo completo tenue (zona de tiempo transcurrido)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(White.copy(alpha = 0.15f))
        )

        // Capa 2 – relleno de tiempo restante, anclado a la izquierda y se reduce
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progresoRestante.coerceIn(0f, 1f))
                .align(Alignment.CenterStart)
                .background(White.copy(alpha = 0.45f))
        )

        // Capa 3 – texto centrado sobre el fondo
        Text(
            text = "Cerrar ($segundosRestantes)",
            color = White,
            fontWeight = FontWeight.Medium,
            fontSize = TextSizeMedium
        )
    }
}

// --- Previews ---

// GuardarMenuOverlayContent

@Preview(showBackground = true, name = "GuardarMenuOverlay - Loading Claro")
@Composable
private fun GuardarMenuOverlayLoadingClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GuardarMenuOverlayContent(
            saveState = SaveUiState.Loading,
            onCompartir = {},
            onMenuGuardado = {}
        )
    }
}

@Preview(showBackground = true, name = "GuardarMenuOverlay - Loading Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GuardarMenuOverlayLoadingOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        GuardarMenuOverlayContent(
            saveState = SaveUiState.Loading,
            onCompartir = {},
            onMenuGuardado = {}
        )
    }
}

@Preview(showBackground = true, name = "GuardarMenuOverlay - Success Claro")
@Composable
private fun GuardarMenuOverlaySuccessClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        GuardarMenuOverlayContent(
            saveState = SaveUiState.Success(menuId = 1, imagenFile = null),
            onCompartir = {},
            onMenuGuardado = {}
        )
    }
}

@Preview(showBackground = true, name = "GuardarMenuOverlay - Success Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GuardarMenuOverlaySuccessOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        GuardarMenuOverlayContent(
            saveState = SaveUiState.Success(menuId = 1, imagenFile = null),
            onCompartir = {},
            onMenuGuardado = {}
        )
    }
}

// LoadingContent

@Preview(showBackground = true, name = "LoadingContent - Claro")
@Composable
private fun LoadingContentClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            LoadingContent(textoColor = Color(0xFF1A1A1A))
        }
    }
}

@Preview(showBackground = true, name = "LoadingContent - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LoadingContentOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            LoadingContent(textoColor = White)
        }
    }
}

// SuccessContent

@Preview(showBackground = true, name = "SuccessContent - Claro")
@Composable
private fun SuccessContentClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            SuccessContent(
                imagenFile = File("fake.jpg"),
                checkProgress = 1f,
                progresoRestante = 0.7f,
                segundosRestantes = 21,
                onMenuGuardado = {},
                onCompartir = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SuccessContent - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SuccessContentOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
        ) {
            SuccessContent(
                imagenFile = File("fake.jpg"),
                checkProgress = 1f,
                progresoRestante = 0.7f,
                segundosRestantes = 21,
                onMenuGuardado = {},
                onCompartir = {}
            )
        }
    }
}

// CerrarButton

@Preview(showBackground = true, name = "CerrarButton - Claro")
@Composable
private fun CerrarButtonClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsAppGreen)
                .padding(SpacingLarge),
            contentAlignment = Alignment.Center
        ) {
            CerrarButton(
                progresoRestante = 0.7f,
                segundosRestantes = 21,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "CerrarButton - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CerrarButtonOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsAppGreen)
                .padding(SpacingLarge),
            contentAlignment = Alignment.Center
        ) {
            CerrarButton(
                progresoRestante = 0.7f,
                segundosRestantes = 21,
                onClick = {}
            )
        }
    }
}
