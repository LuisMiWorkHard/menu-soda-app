package com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.fullwar.menuapp.R
import com.fullwar.menuapp.di.Constants
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.PlatoUpdateRequestDto
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto
import com.fullwar.menuapp.domain.model.TipoPlato
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.*
import java.io.File

@Composable
fun PlatoFormContent(
    viewModel: PlatoViewModel,
    onSelectExisting: ((PlatoResponseDto) -> Unit)? = null
) {
    val context = LocalContext.current

    val nombre = viewModel.formFields.fields["platnom"] as? TextFieldValue ?: TextFieldValue()
    val descripcion = viewModel.formFields.fields["platdes"] as? TextFieldValue ?: TextFieldValue()
    val tipoSeleccionado = viewModel.formFields.fields["codtippla"] as? TipoPlato
    val imageUri = viewModel.formFields.fields["imageUri"] as? Uri
    val tiposState = viewModel.tiposPlatoState
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
            title = { Text(stringResource(R.string.plato_foto_source_title)) },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(stringResource(R.string.plato_foto_camara), color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(stringResource(R.string.plato_foto_galeria), color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Área de foto
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
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
                    text = stringResource(R.string.plato_subir_foto),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = TextSizeSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(SpacingXLarge))

    // Campo: Nombre del plato
    Text(
        text = stringResource(R.string.plato_nombre_label),
        fontWeight = FontWeight.Medium,
        fontSize = TextSizeMedium
    )
    Spacer(modifier = Modifier.height(SpacingSmall))
    OutlinedTextField(
        value = nombre,
        onValueChange = { viewModel.updateField("platnom", it.copy(text = it.text.uppercase())) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && onSelectExisting != null && !viewModel.isEditMode) {
                    val text = (viewModel.formFields.fields["platnom"] as? TextFieldValue)?.text ?: ""
                    if (text.isNotBlank()) viewModel.checkForDuplicates(text)
                } else if (focusState.isFocused) {
                    viewModel.clearDuplicateMatches()
                }
            },
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

    val duplicateMatches = viewModel.duplicateMatches
    if (onSelectExisting != null && duplicateMatches.isNotEmpty()) {
        Spacer(modifier = Modifier.height(SpacingSmall))
        Surface(
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(CornerRadiusMedium),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(SpacingMedium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                    Spacer(modifier = Modifier.width(SpacingSmall))
                    Text(
                        text = "¿Ya existe algo similar?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = TextSizeMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(SpacingSmall))
                duplicateMatches.forEach { dto ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (dto.imagenId != null) {
                                AsyncImage(
                                    model = "${Constants.BASE_URL}/api/imagen/${dto.imagenId}/contenido",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(CornerRadiusSmall)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(SpacingSmall))
                            }
                            Text(
                                text = dto.nombre,
                                fontSize = TextSizeSmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        TextButton(onClick = {
                            onSelectExisting(dto)
                            viewModel.clearDuplicateMatches()
                        }) {
                            Text(
                                text = "Usar este →",
                                fontSize = TextSizeSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(SpacingXSmall))
                TextButton(
                    onClick = { viewModel.clearDuplicateMatches() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continuar creando",
                        fontSize = TextSizeSmall,
                        color = HeavyGray
                    )
                }
            }
        }
    }

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
        onValueChange = { viewModel.updateField("platdes", it.copy(text = it.text.uppercase())) },
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
            ErrorBanner(message = tiposState.message)
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
    override suspend fun searchPlatos(query: String): List<PlatoResponseDto> =
        getPlatos().filter { it.nombre.contains(query, ignoreCase = true) }
    override suspend fun findSimilarPlatos(nombre: String, excludeId: Int?): List<PlatoResponseDto> = emptyList()
    override suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto = throw NotImplementedError()
    override suspend fun updatePlato(id: Int, request: PlatoUpdateRequestDto) = throw NotImplementedError()
    override suspend fun getTiposPlato(): List<TipoPlatoResponseDto> = listOf(
        TipoPlatoResponseDto(1, "Carnes", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(2, "Pescados", 1, "01/01/2024", "admin"),
        TipoPlatoResponseDto(3, "Vegetariano", 1, "01/01/2024", "admin")
    )
    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String, extension: String): ImagenResponseDto = throw NotImplementedError()
}

private val fakePlato = PlatoResponseDto(
    id = 1,
    nombre = "Lomo Saltado",
    descripcion = "Clásico plato peruano",
    tipoPlatoId = 1,
    estadoId = 1,
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

@Preview(showBackground = true, name = "PlatoForm - Con datos - Claro")
@Composable
private fun PlatoFormConDatosPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForEdit(fakePlato)
        vm.loadTiposPlato()
    }
    PreviewWrapper(darkTheme = false) { FormScrollWrapper(vm) }
}

@Preview(showBackground = true, name = "PlatoForm - Con datos - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlatoFormConDatosDarkPreview() {
    val vm = remember { PlatoViewModel(FakePlatoRepository()) }
    LaunchedEffect(Unit) {
        vm.initForEdit(fakePlato)
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
