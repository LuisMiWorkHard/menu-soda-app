package com.fullwar.menuapp.presentation.features.login

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import com.fullwar.menuapp.presentation.navigation.AppScreens
import com.fullwar.menuapp.ui.theme.ButtonHeightLarge
import com.fullwar.menuapp.ui.theme.ButtonHeightMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.IconSize3XLarge
import com.fullwar.menuapp.ui.theme.IconSizeSmall
import com.fullwar.menuapp.ui.theme.LigthGray
import com.fullwar.menuapp.ui.theme.SodaGrayLight
import com.fullwar.menuapp.ui.theme.SodaOrangeLight
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.Spacing3XLarge
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSize3XLarge
import com.fullwar.menuapp.ui.theme.WhatsAppGreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }

    // Launcher para solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        viewModel.onLocationPermisoResultado(allGranted)
    }

    // Solicitar permisos al cargar la pantalla
    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted || !coarseLocationGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Permisos ya concedidos, actualizar ubicación
            viewModel.onLocationPermisoResultado(true)
        }
    }

    // Observar loginState para navegar al Home tras login exitoso
    LaunchedEffect(viewModel.loginState) {
        if (viewModel.loginState is State.Success) {
            navController.navigate(AppScreens.HomeScreen.route) {
                popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
            }
        }
    }

    var documentType by remember {
        mutableStateOf(
            viewModel.formFields.fields["TipoDocumento"] as? TipoDocumento
                ?: viewModel.tiposDocumento[0]
        )
    }
    var documentNumber by remember {
        mutableStateOf(
            (viewModel.formFields.fields["numeroDocumento"] as? TextFieldValue)?.text ?: ""
        )
    }
    var password by remember {
        mutableStateOf(
            (viewModel.formFields.fields["contrasena"] as? TextFieldValue)?.text ?: ""
        )
    }
    var passwordVisible by remember { mutableStateOf(false) }

    val errors = viewModel.formFields.errors
    val serverErrors = viewModel.formFields.serverErrors
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SodaGrayLight)
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(SpacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing3XLarge))

            // Icon Header
            Surface(
                modifier = Modifier.size(IconSize3XLarge),
                shape = RoundedCornerShape(CornerRadiusLarge),
                color = SodaOrangeLight
            ) {
                Icon(imageVector = Icons.Filled.Dining, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Title and Subtitle
            Text(
                text = stringResource(id = R.string.login_welcome_back),
                fontSize = TextSize3XLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(SpacingSmall))
            Text(
                text = stringResource(id = R.string.login_subtitle),
                fontSize = TextSizeMedium,
                color = LigthGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing3XLarge))

            // Document Type Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.login_document_type),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextSizeMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(SpacingMedium))
                SegmentedControl(
                    options = viewModel.tiposDocumento,
                    selectedOption = documentType,
                    onOptionSelected = {
                        documentType = it
                        viewModel.updateField("TipoDocumento", it)
                        documentNumber = ""
                        viewModel.updateField("numeroDocumento", TextFieldValue(""))
                    }
                )
            }

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Document Number Field
            LoginTextField(
                label = stringResource(id = R.string.login_document_number),
                value = documentNumber,
                onValueChange = { input ->
                    val maxLength = when (documentType.tipoDocumento) {
                        1 -> 8   // DNI: 8 dígitos
                        3 -> 11  // RUC: 11 dígitos
                        else -> 20  // CE: hasta 20 caracteres
                    }
                    val filtered = when (documentType.tipoDocumento) {
                        1, 3 -> input.filter { it.isDigit() }  // DNI, RUC: solo dígitos
                        else -> input.filter { it.isLetterOrDigit() }  // CE: alfanumérico
                    }.take(maxLength)
                    documentNumber = filtered
                    viewModel.updateField("numeroDocumento", TextFieldValue(filtered))
                },
                placeholder = stringResource(id = R.string.login_document_number_placeholder),
                leadingIcon = Icons.Filled.Badge,
                keyboardOptions = KeyboardOptions(
                    keyboardType = when (documentType.tipoDocumento) {
                        1, 3 -> KeyboardType.Number  // DNI, RUC
                        else -> KeyboardType.Text     // CE
                    },
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                error = errors["numeroDocumento"],
                serverError = serverErrors["numeroDocumento"]
            )

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Password Field
            LoginTextField(
                label = stringResource(id = R.string.login_password),
                value = password,
                onValueChange = {
                    val limited = it.take(255)
                    password = limited
                    viewModel.updateField("contrasena", TextFieldValue(limited))
                },
                placeholder = stringResource(id = R.string.login_password_placeholder),
                leadingIcon = null,
                modifier = Modifier.focusRequester(passwordFocusRequester),
                trailingIcon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                onTrailingIconClick = { passwordVisible = !passwordVisible },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                isPasswordField = true,
                error = errors["contrasena"],
                serverError = serverErrors["contrasena"]
            )

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Login Button
            Button(
                onClick = { viewModel.login() },
                enabled = viewModel.loginState !is State.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightLarge),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(CornerRadiusMedium)
            ) {
                if (viewModel.loginState is State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.login_button),
                            fontSize = TextSizeLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(SpacingSmall))
                        Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null)
                    }
                }
            }

            // Error de login
            val currentState = viewModel.loginState
            if (currentState is State.Error) {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = TextSizeSmall,
                    modifier = Modifier.padding(top = SpacingSmall)
                )
            }

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // WhatsApp Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = WhatsAppGreen,
                    modifier = Modifier.size(IconSizeSmall)
                )
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text(
                    text = stringResource(id = R.string.login_whatsapp_ready),
                    color = LigthGray,
                    fontSize = TextSizeSmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Row {
                Text(
                    text = stringResource(id = R.string.login_footer_no_account),
                    color = LigthGray,
                    modifier = Modifier.padding(end = SpacingSmall)
                )
                Text(
                    text = stringResource(id = R.string.login_footer_signup),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SegmentedControl(
    options: List<TipoDocumento>,
    selectedOption: TipoDocumento,
    onOptionSelected: (TipoDocumento) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = SodaGrayLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(ButtonHeightMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(SpacingXSmall)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(CornerRadiusSmall)
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.descripcionDocumento,
                        color = if (isSelected) Color.White else LigthGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPasswordField: Boolean = false,
    error: Int? = null,
    serverError: String? = null
) {
    val hasError = serverError != null || error != null

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                fontSize = TextSizeMedium,
                color = Color.Black
            )
            if (isPasswordField) {
                Text(
                    text = stringResource(id = R.string.login_forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = TextSizeSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(CornerRadiusMedium)
                ),
            placeholder = { Text(text = placeholder, color = LigthGray) },
            shape = RoundedCornerShape(CornerRadiusMedium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = SodaGrayLight.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            trailingIcon = {
                if (trailingIcon != null) {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = LigthGray
                        )
                    }
                } else if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = LigthGray)
                }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            isError = hasError
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
