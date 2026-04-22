package com.fullwar.menuapp.presentation.features.menu.estilo

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.em
import androidx.core.content.FileProvider
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.fontFamilyFromString
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.menu.MenuViewModel
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.DeepCharcoal
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.IconSizeSmall
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.fullwar.menuapp.ui.theme.RichBlack
import com.fullwar.menuapp.ui.theme.Shadow
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import com.fullwar.menuapp.ui.theme.White

@Composable
fun SeleccionEstiloScreen(
    menuViewModel: MenuViewModel,
    pasoEstiloViewModel: SeleccionEstiloViewModel,
    modifier: Modifier = Modifier,
    onMenuGuardado: () -> Unit = {}
) {
    val entradas = menuViewModel.selectedEntradas.toList()
    val platos = menuViewModel.selectedPlatosFuertes.toList()
    val imagenesState = pasoEstiloViewModel.imagenesState
    val selectedImagenId = pasoEstiloViewModel.selectedImagenId
    val saveState = pasoEstiloViewModel.saveState
    val triggerCapture = pasoEstiloViewModel.triggerCapture

    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(Unit) { pasoEstiloViewModel.loadImagenes() }

    LaunchedEffect(triggerCapture) {
        if (!triggerCapture) return@LaunchedEffect
        val bitmap = graphicsLayer.toImageBitmap()
        val imagenFile = withContext(Dispatchers.IO) {
            saveBitmapToCache(context, bitmap.asAndroidBitmap())
        }
        pasoEstiloViewModel.guardarMenuDiario(entradas, platos, imagenFile)
        pasoEstiloViewModel.onCaptureHandled()
    }

    Box(modifier = modifier.fillMaxSize()) {
        SeleccionEstiloContent(
            imagenesState = imagenesState,
            selectedImagenId = selectedImagenId,
            entradas = entradas,
            platos = platos,
            previewCaptureModifier = Modifier.drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(graphicsLayer)
            },
            onSelectImagen = { pasoEstiloViewModel.selectImagen(it) },
            onRetry = { pasoEstiloViewModel.loadImagenes() },
            modifier = Modifier.fillMaxSize()
        )
    }

    if (saveState is SaveUiState.Error) {
        AlertDialog(
            onDismissRequest = { pasoEstiloViewModel.resetSaveState() },
            title = { Text("Error al guardar") },
            text = { Text(saveState.message) },
            confirmButton = {
                TextButton(onClick = { pasoEstiloViewModel.resetSaveState() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (saveState is SaveUiState.Loading || saveState is SaveUiState.Success) {
        GuardarMenuOverlay(
            saveState = saveState,
            onCompartir = { file ->
                compartirImagen(context, file)
                onMenuGuardado()
                pasoEstiloViewModel.resetSaveState()
            },
            onMenuGuardado = {
                onMenuGuardado()
                pasoEstiloViewModel.resetSaveState()
            }
        )
    }
}

@Composable
private fun SeleccionEstiloContent(
    imagenesState: State<List<MenuImagenResponseDto>>,
    selectedImagenId: Int?,
    entradas: List<EntradaResponseDto>,
    platos: List<PlatoResponseDto>,
    onSelectImagen: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    previewCaptureModifier: Modifier = Modifier
) {
    val selectedImagen = (imagenesState as? State.Success)
        ?.data?.firstOrNull { it.id == selectedImagenId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(SpacingMedium)
    ) {
        item { Spacer(modifier = Modifier.height(SpacingSmall)) }

        item {
            Text(
                text = "Vista previa del menú",
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (selectedImagenId != null && selectedImagen != null) {
            item {
                Box(modifier = Modifier.clip(RoundedCornerShape(CornerRadiusMedium))) {
                    Box(modifier = previewCaptureModifier) {
                        MenuPreviewCard(
                            imagen = selectedImagen,
                            entradas = entradas,
                            platos = platos
                        )
                    }
                }
            }
        }

        item { ResumenSeleccionRow(cantidadEntradas = entradas.size, cantidadPlatos = platos.size) }

        item { Spacer(modifier = Modifier.height(SpacingSmall)) }

        item {
            Text(
                text = "Elige el fondo visual",
                fontSize = TextSizeXLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        when (imagenesState) {
            is State.Initial, is State.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            is State.Error -> {
                item {
                    ErrorBanner(
                        message = imagenesState.message,
                        modifier = Modifier.padding(vertical = SpacingSmall),
                        onRetry = onRetry
                    )
                }
            }
            is State.Success -> {
                val imagenes = imagenesState.data
                imagenes.chunked(2).forEach { fila ->
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                            fila.forEach { imagen ->
                                ImagenFondoCard(
                                    imagen = imagen,
                                    isSelected = imagen.id == selectedImagenId,
                                    modifier = Modifier.weight(1f),
                                    onClick = onSelectImagen
                                )
                            }
                            if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(SpacingLarge)) }
    }
}

@Composable
fun MenuPreviewCard(
    imagen: MenuImagenResponseDto,
    entradas: List<EntradaResponseDto>,
    platos: List<PlatoResponseDto>
) {
    var cardSizePx by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onSizeChanged { cardSizePx = it }
    ) {
        // Capa 1: Imagen de fondo
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imagen.imagenUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter,
            loading = {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Brush.verticalGradient(listOf(Shadow, DeepCharcoal)))
                )
            }
        )

        if (cardSizePx != IntSize.Zero) {
            val cardH = with(density) { cardSizePx.height.toDp() }

            // Capa 2: Secciones de texto con SubcomposeLayout para distribución exacta de alturas
            val topDp    = with(density) { (cardSizePx.height * imagen.areaTextoTop).toInt().toDp() }
            val bottomDp = with(density) { (cardSizePx.height * imagen.areaTextoBottom).toInt().toDp() }
            val startDp  = with(density) { (cardSizePx.width  * imagen.areaTextoInicio).toInt().toDp() }
            val endDp    = with(density) { (cardSizePx.width  * imagen.areaTextoFin).toInt().toDp() }

            val entradasText = buildMenuText(entradas.map { it.nombre.toSmartUpperCase() })
            val platosText   = buildMenuText(platos.map   { it.nombre.toSmartUpperCase() })
            val maxFontSize       = imagen.maxFontSize.sp
            val fontFamilyEtiqueta  = FontFamily(Font(R.font.rubik_microbe_regular))
            val fontFamilyContenido = fontFamilyFromString(imagen.fontFamily)

            SubcomposeLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardH)
                    .padding(top = topDp, bottom = bottomDp, start = startDp, end = endDp)
            ) { constraints ->
                val available = constraints.maxHeight
                val measureConstraints = Constraints(maxWidth = constraints.maxWidth)

                // Pase 1: altura natural de cada sección sin restricción de alto
                val entradasNatural = subcompose("entradas_m") {
                    SeccionMenu(
                        etiqueta = "Entradas",
                        contenido = entradasText,
                        maxFontSize = maxFontSize,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido,
                        fillAvailableHeight = false
                    )
                }[0].measure(measureConstraints).height

                val platosNatural = subcompose("platos_m") {
                    SeccionMenu(
                        etiqueta = "Segundos",
                        contenido = platosText,
                        maxFontSize = maxFontSize,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido,
                        fillAvailableHeight = false
                    )
                }[0].measure(measureConstraints).height

                // Decidir alturas finales:
                // eHByCount: espacio proporcional a la cantidad de ítems de cada seccion
                // entradasH: el menor entre el espacio natural y el proporcional,
                //             evitando que entradas reciba mas espacio del que ocupa
                //             (si gana el proporcional, AutoSizeText reduce la fuente para caber)
                val eHByCount = (available * entradas.size.coerceAtLeast(1).toFloat() /
                    (entradas.size.coerceAtLeast(1) + platos.size.coerceAtLeast(1)).toFloat()).toInt()
                val entradasH = minOf(entradasNatural, eHByCount)
                val platosH = available - entradasH

                // Pase 2: renderizado final con altura restringida
                // Entradas: sin weight(1f), el texto se envuelve a su altura real
                val entradasFinal = subcompose("entradas_f") {
                    SeccionMenu(
                        etiqueta = "Entradas",
                        contenido = entradasText,
                        maxFontSize = maxFontSize,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido,
                        fillAvailableHeight = false,
                        resetKey = entradasH
                    )
                }[0].measure(Constraints(
                    minWidth = constraints.maxWidth, maxWidth = constraints.maxWidth,
                    minHeight = 0, maxHeight = entradasH
                ))

                // Platos: llena todo el espacio restante real
                val actualPlatosH = constraints.maxHeight - entradasFinal.height
                val platosFinal = subcompose("platos_f") {
                    SeccionMenu(
                        etiqueta = "Segundos",
                        contenido = platosText,
                        maxFontSize = maxFontSize,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido,
                        resetKey = actualPlatosH
                    )
                }[0].measure(Constraints(
                    minWidth = constraints.maxWidth, maxWidth = constraints.maxWidth,
                    minHeight = actualPlatosH, maxHeight = actualPlatosH
                ))

                layout(constraints.maxWidth, constraints.maxHeight) {
                    entradasFinal.placeRelative(0, 0)
                    platosFinal.placeRelative(0, entradasFinal.height)
                }
            }
        }
    }
}

@Composable
private fun SeccionMenu(
    etiqueta: String,
    contenido: AnnotatedString,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 17.sp,
    fontFamilyEtiqueta: FontFamily = FontFamily.Default,
    fontFamily: FontFamily = FontFamily.Default,
    fillAvailableHeight: Boolean = true,
    resetKey: Any = Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpacingXSmall),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = etiqueta,
            fontSize = (maxFontSize.value + 1f).sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamilyEtiqueta,
            color = White.copy(alpha = 0.7f),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(2.dp))
        val textModifier = if (fillAvailableHeight)
            Modifier.fillMaxWidth().weight(1f)
        else
            Modifier.fillMaxWidth()
        AutoSizeText(
            text = contenido,
            resetKey = resetKey,
            modifier = textModifier,
            maxFontSize = maxFontSize,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun AutoSizeText(
    text: AnnotatedString,
    resetKey: Any = Unit,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 17.sp,
    minFontSize: TextUnit = 8.sp,
    color: Color = White,
    fontWeight: FontWeight = FontWeight.Bold,
    fontFamily: FontFamily = FontFamily.Default,
    textAlign: TextAlign = TextAlign.Start
) {
    var fontSize by remember(text, maxFontSize, resetKey) { mutableStateOf(maxFontSize) }

    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        textAlign = textAlign,
        overflow = TextOverflow.Clip,
        onTextLayout = { result ->
            if (result.didOverflowHeight && fontSize > minFontSize) {
                fontSize = (fontSize.value * 0.85f).coerceAtLeast(minFontSize.value).sp
            }
        }
    )
}

private fun buildMenuText(items: List<String>): AnnotatedString =
    if (items.isEmpty()) {
        AnnotatedString("—")
    } else {
        buildAnnotatedString {
            items.forEachIndexed { index, item ->
                withStyle(ParagraphStyle(textIndent = TextIndent(restLine = 0.85.em))) {
                    append("· $item")
                }
            }
        }
    }

@Composable
fun ResumenSeleccionRow(cantidadEntradas: Int, cantidadPlatos: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(CornerRadiusMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.room_service_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSizeMedium)
            )
            Spacer(modifier = Modifier.width(SpacingSmall))
            Column {
                Text(
                    text = "Resumen de selección",
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.paso_estilo_resumen_desc, cantidadEntradas, cantidadPlatos),
                    fontSize = TextSizeSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ImagenFondoCard(
    imagen: MenuImagenResponseDto,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    var showPreview by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(CornerRadiusMedium))
            .border(3.dp, borderColor, RoundedCornerShape(CornerRadiusMedium))
            .clickable { onClick(imagen.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Shadow, DeepCharcoal)))
        )

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imagen.imagenUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpacingSmall)
                .size(28.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary else RichBlack.copy(alpha = 0.55f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(IconSizeSmall)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(SpacingSmall)
                .size(42.dp)
                .background(RichBlack.copy(alpha = 0.55f), CircleShape)
                .clickable { showPreview = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ZoomIn,
                contentDescription = "Ver imagen completa",
                tint = White,
                modifier = Modifier.size(IconSizeMedium)
            )
        }
    }

    if (showPreview) {
        ImagenFondoPreviewDialog(
            imagenUrl = imagen.imagenUrl,
            onDismiss = { showPreview = false }
        )
    }
}

@Composable
private fun ImagenFondoPreviewDialog(
    imagenUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(CornerRadiusMedium))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Brush.verticalGradient(listOf(Shadow, DeepCharcoal)))
            )

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = White, strokeWidth = 2.dp)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(SpacingSmall)
                    .size(32.dp)
                    .background(RichBlack.copy(alpha = 0.6f), CircleShape)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = White,
                    modifier = Modifier.size(IconSizeSmall)
                )
            }
        }
    }
}

private fun saveBitmapToCache(context: android.content.Context, bitmap: Bitmap): File? {
    return try {
        val dir = File(context.cacheDir, "menu_images").also { it.mkdirs() }
        val file = File(dir, "menu_preview_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file
    } catch (e: Exception) {
        null
    }
}

private fun compartirImagen(context: android.content.Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir menú"))
}

// --- Datos fake para previews ---

private val fakeEntradas = listOf(
    EntradaResponseDto(id = 1, nombre = "Gazpacho", descripcion = "", tipoEntradaId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    EntradaResponseDto(id = 2, nombre = "Ensalada Mixta", descripcion = "", tipoEntradaId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
)

private val fakePlatos = listOf(
    PlatoResponseDto(id = 1, nombre = "Paella", descripcion = "", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    PlatoResponseDto(id = 2, nombre = "Filete de Ternera", descripcion = "", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    PlatoResponseDto(id = 3, nombre = "Salmón a la Plancha", descripcion = "", tipoPlatoId = 1, estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin")
)

private val fakeImagenes = listOf(
    MenuImagenResponseDto(id = 1, imagenId = 1, imagenUrl = "", estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    MenuImagenResponseDto(id = 2, imagenId = 2, imagenUrl = "", estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    MenuImagenResponseDto(id = 3, imagenId = 3, imagenUrl = "", estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
    MenuImagenResponseDto(id = 4, imagenId = 4, imagenUrl = "", estadoId = 1, fechaRegistro = "01/01/2024", usuarioRegistro = "admin"),
)

// --- Previews: SeleccionEstiloContent ---

@Preview(showBackground = true, name = "SeleccionEstilo - Loading Claro")
@Composable
private fun SeleccionEstiloLoadingPreview() {
    MenuAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Loading,
                selectedImagenId = null,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Loading Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionEstiloLoadingDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Loading,
                selectedImagenId = null,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Error Claro")
@Composable
private fun SeleccionEstiloErrorPreview() {
    MenuAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Error("Sin conexión al servidor"),
                selectedImagenId = null,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Error Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionEstiloErrorDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Error("Sin conexión al servidor"),
                selectedImagenId = null,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Sin Selección Claro")
@Composable
private fun SeleccionEstiloSuccessPreview() {
    MenuAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Success(fakeImagenes),
                selectedImagenId = null,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Con Selección Claro")
@Composable
private fun SeleccionEstiloConSeleccionPreview() {
    MenuAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Success(fakeImagenes),
                selectedImagenId = fakeImagenes[0].id,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SeleccionEstilo - Con Selección Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SeleccionEstiloConSeleccionDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SeleccionEstiloContent(
                imagenesState = State.Success(fakeImagenes),
                selectedImagenId = fakeImagenes[0].id,
                entradas = fakeEntradas,
                platos = fakePlatos,
                onSelectImagen = {},
                onRetry = {}
            )
        }
    }
}

// --- Previews: MenuPreviewCard ---

@Preview(showBackground = true, name = "MenuPreviewCard - Claro")
@Composable
private fun MenuPreviewCardPreview() {
    MenuAppTheme(darkTheme = false) {
        MenuPreviewCard(imagen = fakeImagenes[0], entradas = fakeEntradas, platos = fakePlatos)
    }
}

@Preview(showBackground = true, name = "MenuPreviewCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MenuPreviewCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            MenuPreviewCard(imagen = fakeImagenes[0], entradas = fakeEntradas, platos = fakePlatos)
        }
    }
}

// --- Previews: ResumenSeleccionRow ---

@Preview(showBackground = true, name = "ResumenSeleccionRow - Claro")
@Composable
private fun ResumenSeleccionRowPreview() {
    MenuAppTheme(darkTheme = false) {
        ResumenSeleccionRow(cantidadEntradas = 2, cantidadPlatos = 3)
    }
}

@Preview(showBackground = true, name = "ResumenSeleccionRow - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ResumenSeleccionRowDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResumenSeleccionRow(cantidadEntradas = 2, cantidadPlatos = 3)
        }
    }
}

// --- Previews: ImagenFondoCard ---

@Preview(showBackground = true, name = "ImagenFondoCard - Claro")
@Composable
private fun ImagenFondoCardPreview() {
    MenuAppTheme(darkTheme = false) {
        Row(
            modifier = Modifier.padding(SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
        ) {
            ImagenFondoCard(imagen = fakeImagenes[0], isSelected = true, modifier = Modifier.weight(1f)) {}
            ImagenFondoCard(imagen = fakeImagenes[1], isSelected = false, modifier = Modifier.weight(1f)) {}
        }
    }
}

@Preview(showBackground = true, name = "ImagenFondoCard - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ImagenFondoCardDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(SpacingMedium),
                horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
            ) {
                ImagenFondoCard(imagen = fakeImagenes[2], isSelected = false, modifier = Modifier.weight(1f)) {}
                ImagenFondoCard(imagen = fakeImagenes[3], isSelected = true, modifier = Modifier.weight(1f)) {}
            }
        }
    }
}

// --- Previews: ImagenFondoPreviewDialog ---

@Preview(showBackground = true, name = "ImagenFondoPreviewDialog - Claro")
@Composable
private fun ImagenFondoPreviewDialogPreview() {
    MenuAppTheme(darkTheme = false) {
        ImagenFondoPreviewDialog(imagenUrl = "", onDismiss = {})
    }
}

@Preview(showBackground = true, name = "ImagenFondoPreviewDialog - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ImagenFondoPreviewDialogDarkPreview() {
    MenuAppTheme(darkTheme = true) {
        ImagenFondoPreviewDialog(imagenUrl = "", onDismiss = {})
    }
}
