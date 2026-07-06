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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.ZoomIn
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
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
import com.fullwar.menuapp.presentation.common.components.ImagenFondoPreviewDialog
import com.fullwar.menuapp.presentation.common.components.MenuSodaDialog
import com.fullwar.menuapp.presentation.common.components.MenuSodaDialogVariant
import com.fullwar.menuapp.presentation.common.utils.fontFamilyFromString
import com.fullwar.menuapp.presentation.common.utils.toSmartUpperCase
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fullwar.menuapp.presentation.common.components.ShimmerBox
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

// Ajustes de tipografía de la carta:
// TITLE_FONT_OFFSET_SP: cuánto más grande es el título (ENTRADAS/SEGUNDOS) que los ítems.
// BASE_LINE_HEIGHT_EM:  interlineado compacto de los ítems (el espacio se llena con la fuente, no separando líneas).
// MIN_FONT_SIZE / MAX_FONT_SIZE: límites de la fuente; crece hasta llenar el área sin desperdiciar espacio.
private const val TITLE_FONT_OFFSET_SP = 4f
private const val BASE_LINE_HEIGHT_EM = 1.2f
private val MIN_FONT_SIZE = 8.sp
private val MAX_FONT_SIZE = 40.sp

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
    val toastImagenNoDisponible = stringResource(R.string.toast_imagen_no_disponible)
    val toastCapturaError = stringResource(R.string.toast_captura_error)

    LaunchedEffect(Unit) { pasoEstiloViewModel.loadImagenes() }

    LaunchedEffect(imagenesState) {
        if (imagenesState !is State.Success) return@LaunchedEffect
        val newIds = imagenesState.data.map { it.id }.toSet()
        val currentId = pasoEstiloViewModel.selectedImagenId
        if (currentId != null && currentId !in newIds) {
            pasoEstiloViewModel.clearSelectedImagen()
            Toast.makeText(context, toastImagenNoDisponible, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(triggerCapture) {
        if (!triggerCapture) return@LaunchedEffect
        try {
            // Garantiza que el último frame (fondo + textos) quedó registrado en la capa.
            withFrameNanos { }
            val bitmap = graphicsLayer.toImageBitmap()
            val imagenFile = withContext(Dispatchers.IO) {
                saveBitmapToCache(context, bitmap.asAndroidBitmap())
            }
            if (menuViewModel.isEditMode && menuViewModel.menuId != null) {
                pasoEstiloViewModel.actualizarMenuDiario(
                    menuId = menuViewModel.menuId!!,
                    entradas = entradas,
                    platos = platos,
                    imagenId = selectedImagenId,
                    imagenFile = imagenFile
                )
            } else {
                pasoEstiloViewModel.guardarMenuDiario(
                    entradas       = entradas,
                    platos         = platos,
                    imagenFile     = imagenFile,
                    menuImagenId   = selectedImagenId,
                    fechaMillis    = menuViewModel.selectedDateMillis,
                    menuToDeleteId = menuViewModel.conflictoMenuId
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, toastCapturaError, Toast.LENGTH_LONG).show()
        } finally {
            pasoEstiloViewModel.onCaptureHandled()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        SeleccionEstiloContent(
            imagenesState = imagenesState,
            selectedImagenId = selectedImagenId,
            entradas = entradas,
            platos = platos,
            isPreviewReady = pasoEstiloViewModel.isPreviewReady,
            onImagenLoaded = { pasoEstiloViewModel.onPreviewRendered() },
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
        MenuSodaDialog(
            title = stringResource(R.string.dialog_error_guardar_titulo),
            message = saveState.message,
            onDismissRequest = { pasoEstiloViewModel.resetSaveState() },
            confirmLabel = stringResource(R.string.dialog_error_guardar_confirmar),
            onConfirm = { pasoEstiloViewModel.resetSaveState() },
            icon = Icons.Filled.ErrorOutline,
            variant = MenuSodaDialogVariant.Error
        )
    }

    if (saveState is SaveUiState.Loading || saveState is SaveUiState.Success) {
        GuardarMenuOverlay(
            saveState = saveState,
            onCompartir = { file ->
                compartirImagen(context, file)
                onMenuGuardado()
            },
            onMenuGuardado = {
                onMenuGuardado()
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
    isPreviewReady: Boolean = false,
    onImagenLoaded: () -> Unit = {},
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadiusMedium))
                ) {
                    Box(modifier = previewCaptureModifier) {
                        MenuPreviewCard(
                            imagen = selectedImagen,
                            entradas = entradas,
                            platos = platos,
                            onImagenLoaded = onImagenLoaded
                        )
                    }
                    AnimatedVisibility(
                        visible = !isPreviewReady,
                        enter = EnterTransition.None,
                        exit = fadeOut(animationSpec = tween(400))
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
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
                items(2) {
                    ImagenFondoCardSkeletonRow()
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
    platos: List<PlatoResponseDto>,
    onImagenLoaded: () -> Unit = {}
) {
    var cardSizePx by remember { mutableStateOf(IntSize.Zero) }
    var isImageReady by remember(imagen.id) { mutableStateOf(false) }
    val density = LocalDensity.current
    val context = LocalContext.current

    CompositionLocalProvider(
        LocalDensity provides Density(density = density.density, fontScale = 1f)
    ) {
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
            success = {
                LaunchedEffect(Unit) {
                    isImageReady = true
                    onImagenLoaded()
                }
                SubcomposeAsyncImageContent()
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

        if (cardSizePx != IntSize.Zero && isImageReady) {
            val cardH = with(density) { cardSizePx.height.toDp() }

            // Capa 2: Secciones de texto con SubcomposeLayout para distribución exacta de alturas
            val topDp    = with(density) { (cardSizePx.height * imagen.areaTextoTop).toInt().toDp() }
            val bottomDp = with(density) { (cardSizePx.height * imagen.areaTextoBottom).toInt().toDp() }
            val startDp  = with(density) { (cardSizePx.width  * imagen.areaTextoInicio).toInt().toDp() }
            val endDp    = with(density) { (cardSizePx.width  * imagen.areaTextoFin).toInt().toDp() }

            val entradasText = buildMenuText(entradas.map { it.nombre.toSmartUpperCase() })
            val platosText   = buildMenuText(platos.map   { it.nombre.toSmartUpperCase() })
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

                fun seccionHeight(slot: String, etiqueta: String, contenido: AnnotatedString, font: TextUnit): Int =
                    subcompose(slot) {
                        SeccionMenu(
                            etiqueta = etiqueta,
                            contenido = contenido,
                            contentFontSize = font,
                            fontFamilyEtiqueta = fontFamilyEtiqueta,
                            fontFamily = fontFamilyContenido
                        )
                    }[0].measure(measureConstraints).height

                // Mayor fuente compartida (igual para entradas y segundos) cuyo bloque apilado
                // llena el área disponible. El espacio se ocupa creciendo la fuente, no el interlineado.
                var lo = MIN_FONT_SIZE.value
                var hi = MAX_FONT_SIZE.value
                var best = MIN_FONT_SIZE.value
                repeat(9) { i ->
                    val mid = (lo + hi) / 2f
                    val total = seccionHeight("e_s$i", "Entradas", entradasText, mid.sp) +
                                seccionHeight("p_s$i", "Segundos", platosText, mid.sp)
                    if (total <= available) { best = mid; lo = mid } else { hi = mid }
                }
                val sharedFont = best.sp

                // Render final: ambas secciones al mismo tamaño, apiladas desde arriba,
                // cada una a su altura natural (ítems compactos, sin huecos internos).
                val entradasFinal = subcompose("e_final") {
                    SeccionMenu(
                        etiqueta = "Entradas",
                        contenido = entradasText,
                        contentFontSize = sharedFont,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido
                    )
                }[0].measure(measureConstraints)

                val platosFinal = subcompose("p_final") {
                    SeccionMenu(
                        etiqueta = "Segundos",
                        contenido = platosText,
                        contentFontSize = sharedFont,
                        fontFamilyEtiqueta = fontFamilyEtiqueta,
                        fontFamily = fontFamilyContenido
                    )
                }[0].measure(measureConstraints)

                layout(constraints.maxWidth, constraints.maxHeight) {
                    entradasFinal.placeRelative(0, 0)
                    platosFinal.placeRelative(0, entradasFinal.height)
                }
            }
        }
    }
    }
}

@Composable
private fun SeccionMenu(
    etiqueta: String,
    contenido: AnnotatedString,
    contentFontSize: TextUnit,
    modifier: Modifier = Modifier,
    fontFamilyEtiqueta: FontFamily = FontFamily.Default,
    fontFamily: FontFamily = FontFamily.Default
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
            fontSize = (contentFontSize.value + TITLE_FONT_OFFSET_SP).sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = fontFamilyEtiqueta,
            color = White.copy(alpha = 0.7f),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(2.dp))
        MenuContenidoText(
            contenido = contenido,
            fontSize = contentFontSize,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun MenuContenidoText(
    contenido: AnnotatedString,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier
) {
    Text(
        text = contenido,
        modifier = modifier.fillMaxWidth(),
        fontSize = fontSize,
        color = White,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Clip
    )
}

private fun buildMenuText(items: List<String>, lineHeightEm: Float = BASE_LINE_HEIGHT_EM): AnnotatedString =
    if (items.isEmpty()) {
        AnnotatedString("—")
    } else {
        buildAnnotatedString {
            items.forEach { item ->
                withStyle(ParagraphStyle(textIndent = TextIndent(restLine = 0.85.em), lineHeight = lineHeightEm.em)) {
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
                imageVector = Icons.Filled.RoomService,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
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
                    color = MaterialTheme.colorScheme.onSurface
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

