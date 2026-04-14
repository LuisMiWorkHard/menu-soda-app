package com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared

import android.Manifest
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.fullwar.menuapp.R
import com.fullwar.menuapp.di.Constants
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto
import com.fullwar.menuapp.domain.model.TipoEntrada
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.*
import java.io.File

@Composable
fun EntradaFormContent(viewModel: EntradaViewModel) {
    val context = LocalContext.current

    val nombre = viewModel.formFields.fields["entdes"] as? TextFieldValue ?: TextFieldValue()
    val descripcion = viewModel.formFields.fields["entdeslar"] as? TextFieldValue ?: TextFieldValue()
    val tipoSeleccionado = viewModel.formFields.fields["codtipent"] as? TipoEntrada
    val imageUri = viewModel.formFields.fields["imageUri"] as? Uri
    val tiposState = viewModel.tiposEntradaState
    val errors: Map<String, Int?> = viewModel.formFields.errors
    val serverErrors: Map<String, String?> = viewModel.formFields.serverErrors
    val currentImagenId = viewModel.currentImagenId

    val descripcionFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            viewModel.updateField("imageUri", cameraUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateField("imageUri", it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoFile = File(context.cacheDir, "camera_photos").apply { mkdirs() }
                .let { File(it, "photo_${System.currentTimeMillis()}.jpg") }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            cameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = { Text(stringResource(R.string.entrada_foto_source_title)) },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(stringResource(R.string.entrada_foto_camara), color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(stringResource(R.string.entrada_foto_galeria), color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Área de foto
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(CornerRadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (imageUri == null && currentImagenId == null) {
                    Modifier.border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        RoundedCornerShape(CornerRadiusMedium)
                    )
                } else Modifier
            )
            .clickable { showPhotoSourceDialog = true },
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUri != null -> AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(CornerRadiusMedium)),
                contentScale = ContentScale.Crop
            )
            currentImagenId != null -> AsyncImage(
                model = "${Constants.BASE_URL}/api/imagen/$currentImagenId/contenido",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(CornerRadiusMedium)),
                contentScale = ContentScale.Crop
            )
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Spacing4XLarge)
                )
                Spacer(modifier = Modifier.height(SpacingSmall))
                Text(
                    text = stringResource(R.string.entrada_subir_foto),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = TextSizeSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(SpacingXLarge))

    // Campo: Nombre de la entrada
    Text(
        text = stringResource(R.string.entrada_nombre_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))
    OutlinedTextField(
        value = nombre,
        onValueChange = { viewModel.updateField("entdes", it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(R.string.entrada_nombre_placeholder),
                color = HeavyGray
            )
        },
        shape = RoundedCornerShape(CornerRadiusMedium),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary
        ),
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
        isError = errors["entdes"] != null || serverErrors["entdes"] != null,
        supportingText = {
            val errorRes = errors["entdes"]
            val serverError = serverErrors["entdes"]
            when {
                errorRes != null -> Text(stringResource(errorRes), color = MaterialTheme.colorScheme.error)
                serverError != null -> Text(serverError, color = MaterialTheme.colorScheme.error)
                else -> Text("${nombre.text.length}/200", color = HeavyGray, fontSize = TextSizeXSmall)
            }
        }
    )

    Spacer(modifier = Modifier.height(SpacingLarge))

    // Campo: Descripción corta
    Text(
        text = stringResource(R.string.entrada_descripcion_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))
    OutlinedTextField(
        value = descripcion,
        onValueChange = { viewModel.updateField("entdeslar", it) },
        modifier = Modifier.fillMaxWidth().focusRequester(descripcionFocusRequester),
        placeholder = {
            Text(
                text = stringResource(R.string.entrada_descripcion_placeholder),
                color = HeavyGray
            )
        },
        shape = RoundedCornerShape(CornerRadiusMedium),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary
        ),
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
        isError = errors["entdeslar"] != null || serverErrors["entdeslar"] != null,
        supportingText = {
            val errorRes = errors["entdeslar"]
            val serverError = serverErrors["entdeslar"]
            when {
                errorRes != null -> Text(stringResource(errorRes), color = MaterialTheme.colorScheme.error)
                serverError != null -> Text(serverError, color = MaterialTheme.colorScheme.error)
                else -> Text("${descripcion.text.length}/1000", color = HeavyGray, fontSize = TextSizeXSmall)
            }
        }
    )

    Spacer(modifier = Modifier.height(SpacingLarge))

    // Tipo de entrada (chips)
    Text(
        text = stringResource(R.string.entrada_tipo_label),
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
        is State.Success<List<TipoEntrada>> -> {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(SpacingSmall),
                verticalArrangement = Arrangement.spacedBy(SpacingSmall)
            ) {
                tiposState.data.forEach { tipo ->
                    val isSelected = tipoSeleccionado?.id == tipo.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateField("codtipent", tipo) },
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

    val tipoError = errors["codtipent"]
    val tipoServerError = serverErrors["codtipent"]
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

private class FakeEntradaRepository : IEntradaRepository {
    override suspend fun getEntradas(): List<EntradaResponseDto> = emptyList()
    override suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto = throw NotImplementedError()
    override suspend fun updateEntrada(id: Int, request: EntradaUpdateRequestDto): EntradaResponseDto = throw NotImplementedError()
    override suspend fun getTiposEntrada(): List<TipoEntradaResponseDto> = listOf(
        TipoEntradaResponseDto(1, "Fría", 1, "01/01/2024", "admin"),
        TipoEntradaResponseDto(2, "Caliente", 1, "01/01/2024", "admin"),
        TipoEntradaResponseDto(3, "Ensalada", 1, "01/01/2024", "admin")
    )
    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String, extension: String): ImagenResponseDto = throw NotImplementedError()
}

private val fakeEntrada = EntradaResponseDto(
    id = 1,
    descripcion = "Ceviche Clásico",
    descripcionLarga = "Fresco, ligero y con toque de limón",
    estadoId = 1,
    tipoEntradaId = 2,
    imagenId = null,
    fechaRegistro = "01/01/2024",
    usuarioRegistro = "admin"
)

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
private fun FormScrollWrapper(viewModel: EntradaViewModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacingXLarge)
            .padding(vertical = SpacingLarge)
    ) {
        EntradaFormContent(viewModel = viewModel)
    }
}

// Estado 1: Vacío (sin tipos cargados)

@Preview(showBackground = true, name = "EntradaForm - Vacío - Claro")
@Composable
private fun EntradaFormVacioPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) { vm.initForCreate() }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "EntradaForm - Vacío - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaFormVacioDarkPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) { vm.initForCreate() }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}

// Estado 2: Con tipos cargados

@Preview(showBackground = true, name = "EntradaForm - Con tipos - Claro")
@Composable
private fun EntradaFormConTiposPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposEntrada()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "EntradaForm - Con tipos - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaFormConTiposDarkPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposEntrada()
    }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}

// Estado 3: Con datos (modo editar)

@Preview(showBackground = true, name = "EntradaForm - Con datos - Claro")
@Composable
private fun EntradaFormConDatosPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForEdit(fakeEntrada)
        vm.loadTiposEntrada()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "EntradaForm - Con datos - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaFormConDatosDarkPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForEdit(fakeEntrada)
        vm.loadTiposEntrada()
    }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}

// Estado 4: Con errores de validación

@Preview(showBackground = true, name = "EntradaForm - Con errores - Claro")
@Composable
private fun EntradaFormConErroresPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposEntrada()
        vm.validate()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "EntradaForm - Con errores - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EntradaFormConErroresDarkPreview() {
    val vm = remember { EntradaViewModel(FakeEntradaRepository()) }
    LaunchedEffect(Unit) {
        vm.initForCreate()
        vm.loadTiposEntrada()
        vm.validate()
    }
    PreviewWrapper(darkTheme = true) { FormScrollWrapper(vm) }
}
