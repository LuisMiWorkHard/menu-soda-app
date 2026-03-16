package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import android.Manifest
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.fullwar.menuapp.R
import com.fullwar.menuapp.domain.model.TipoEntrada
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirEntradaBottomSheet(
    viewModel: EntradaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createState = viewModel.createState
    val tiposState = viewModel.tiposEntradaState

    val nombre = viewModel.formFields.fields["entdes"] as? TextFieldValue ?: TextFieldValue()
    val descripcion = viewModel.formFields.fields["entdeslar"] as? TextFieldValue ?: TextFieldValue()
    val tipoSeleccionado = viewModel.formFields.fields["codtipent"] as? TipoEntrada
    val imageUri = viewModel.formFields.fields["imageUri"] as? Uri

    val errors = viewModel.formFields.errors
    val serverErrors = viewModel.formFields.serverErrors

    // URI para la foto de cámara
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            viewModel.updateField("imageUri", cameraUri)
        }
    }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateField("imageUri", it) }
    }

    // Launcher para permiso de cámara
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

    // Diálogo para elegir fuente de foto
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    // Observar éxito para cerrar el bottom sheet
    LaunchedEffect(createState) {
        if (createState is State.Success) {
            onSuccess()
        }
    }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = { Text(stringResource(R.string.entrada_foto_source_title)) },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text(stringResource(R.string.entrada_foto_camara), color = SodaOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoSourceDialog = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(stringResource(R.string.entrada_foto_galeria), color = SodaOrange)
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SpacingXLarge)
                .padding(bottom = SpacingXXLarge)
        ) {
            // Header: Título + Botón cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.entrada_dialog_titulo),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeXLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = SodaGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingLarge))

            // Área de foto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(CornerRadiusMedium))
                    .background(SodaOrangeLight)
                    .then(
                        if (imageUri == null) {
                            Modifier.border(
                                BorderStroke(2.dp, SodaOrange.copy(alpha = 0.4f)),
                                RoundedCornerShape(CornerRadiusMedium)
                            )
                        } else Modifier
                    )
                    .clickable { showPhotoSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(CornerRadiusMedium)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = SodaOrange,
                            modifier = Modifier.size(Spacing4XLarge)
                        )
                        Spacer(modifier = Modifier.height(SpacingSmall))
                        Text(
                            text = stringResource(R.string.entrada_subir_foto),
                            color = SodaOrange,
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
                        color = SodaGray
                    )
                },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SodaOrange,
                    unfocusedBorderColor = SodaGrayLight,
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                singleLine = true,
                isError = errors["entdes"] != null || serverErrors["entdes"] != null,
                supportingText = {
                    val errorRes = errors["entdes"]
                    val serverError = serverErrors["entdes"]
                    when {
                        errorRes != null -> Text(
                            stringResource(errorRes),
                            color = MaterialTheme.colorScheme.error
                        )
                        serverError != null -> Text(
                            serverError,
                            color = MaterialTheme.colorScheme.error
                        )
                        else -> Text(
                            "${nombre.text.length}/200",
                            color = SodaGray,
                            fontSize = TextSizeXSmall
                        )
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
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.entrada_descripcion_placeholder),
                        color = SodaGray
                    )
                },
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SodaOrange,
                    unfocusedBorderColor = SodaGrayLight,
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                minLines = 3,
                maxLines = 5,
                isError = errors["entdeslar"] != null || serverErrors["entdeslar"] != null,
                supportingText = {
                    val errorRes = errors["entdeslar"]
                    val serverError = serverErrors["entdeslar"]
                    when {
                        errorRes != null -> Text(
                            stringResource(errorRes),
                            color = MaterialTheme.colorScheme.error
                        )
                        serverError != null -> Text(
                            serverError,
                            color = MaterialTheme.colorScheme.error
                        )
                        else -> Text(
                            "${descripcion.text.length}/1000",
                            color = SodaGray,
                            fontSize = TextSizeXSmall
                        )
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
                        color = SodaOrange,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                }
                is State.Success -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(SpacingSmall),
                        verticalArrangement = Arrangement.spacedBy(SpacingSmall)
                    ) {
                        tiposState.data.forEach { tipo ->
                            val isSelected = tipoSeleccionado?.id == tipo.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateField("codtipent", tipo) },
                                label = {
                                    Text(
                                        text = tipo.descripcion,
                                        fontSize = TextSizeSmall
                                    )
                                },
                                shape = RoundedCornerShape(CornerRadiusMedium),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.Transparent,
                                    selectedLabelColor = SodaOrange
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = SodaOrange,
                                    borderColor = SodaGray.copy(alpha = 0.3f)
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

            // Error de tipo de entrada
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

            Spacer(modifier = Modifier.height(SpacingXXLarge))

            // Error general del servidor
            if (createState is State.Error) {
                Surface(
                    color = Color(0xFFFCE4EC),
                    shape = RoundedCornerShape(CornerRadiusSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = SpacingLarge)
                ) {
                    Text(
                        text = (createState as State.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = TextSizeSmall,
                        modifier = Modifier.padding(SpacingMedium)
                    )
                }
            }

            // Botón: Guardar y Seleccionar
            Button(
                onClick = { viewModel.createEntrada(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightLarge),
                shape = RoundedCornerShape(CornerRadiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
                enabled = createState !is State.Loading
            ) {
                if (createState is State.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(IconSizeMedium),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.entrada_guardar),
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
                    text = stringResource(R.string.entrada_cancelar),
                    color = SodaGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextSizeMedium
                )
            }
        }
    }
}
