package com.fullwar.menuapp.presentation.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fullwar.menuapp.R
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.features.shared.SharedViewModel
import com.fullwar.menuapp.ui.theme.ButtonHeightLarge
import com.fullwar.menuapp.ui.theme.ButtonHeightMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.IconSize3XLarge
import com.fullwar.menuapp.ui.theme.IconSizeSmall
import com.fullwar.menuapp.ui.theme.SodaGray
import com.fullwar.menuapp.ui.theme.SodaGrayLight
import com.fullwar.menuapp.ui.theme.SodaOrange
import com.fullwar.menuapp.ui.theme.SodaOrangeLight
import com.fullwar.menuapp.ui.theme.SpacingLarge
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
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SodaGrayLight),
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
                Icon(imageVector = Icons.Filled.Dining, contentDescription = null, tint = SodaOrange)
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
                color = SodaGray,
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
                    }
                )
            }

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Document Number Field
            LoginTextField(
                label = stringResource(id = R.string.login_document_number),
                value = documentNumber,
                onValueChange = {
                    documentNumber = it
                    viewModel.updateField("numeroDocumento", TextFieldValue(it))
                },
                placeholder = stringResource(id = R.string.login_document_number_placeholder),
                leadingIcon = Icons.Filled.Badge,
                error = errors["numeroDocumento"]
            )

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Password Field
            LoginTextField(
                label = stringResource(id = R.string.login_password),
                value = password,
                onValueChange = {
                    password = it
                    viewModel.updateField("contrasena", TextFieldValue(it))
                },
                placeholder = stringResource(id = R.string.login_password_placeholder),
                leadingIcon = null,
                trailingIcon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                onTrailingIconClick = { passwordVisible = !passwordVisible },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isPasswordField = true,
                error = errors["contrasena"]
            )

            Spacer(modifier = Modifier.height(SpacingXLarge))

            // Login Button
            Button(
                onClick = {
                    if (viewModel.validate()) {
                        /* TODO: Login logic */
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeightLarge),
                colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
                shape = RoundedCornerShape(CornerRadiusMedium)
            ) {
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
                    color = SodaGray,
                    fontSize = TextSizeSmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Row {
                Text(
                    text = stringResource(id = R.string.login_footer_no_account),
                    color = SodaGray,
                    modifier = Modifier.padding(end = SpacingSmall)
                )
                Text(
                    text = stringResource(id = R.string.login_footer_signup),
                    color = SodaOrange,
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
                            if (isSelected) SodaOrange else Color.Transparent,
                            shape = RoundedCornerShape(CornerRadiusSmall)
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.descripcionDocumento,
                        color = if (isSelected) Color.White else SodaGray,
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
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPasswordField: Boolean = false,
    error: Int? = null
) {
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
                    color = SodaOrange,
                    fontSize = TextSizeSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(CornerRadiusMedium)
                ),
            placeholder = { Text(text = placeholder, color = SodaGray) },
            shape = RoundedCornerShape(CornerRadiusMedium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SodaOrange,
                unfocusedBorderColor = SodaGrayLight.copy(alpha = 0.5f),
                cursorColor = SodaOrange
            ),
            trailingIcon = {
                if (trailingIcon != null) {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = SodaGray
                        )
                    }
                } else if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = SodaGray)
                }
            },
            visualTransformation = visualTransformation,
            singleLine = true,
            isError = error != null
        )
        if (error != null) {
            Text(
                text = stringResource(id = error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = SpacingSmall, top = SpacingXSmall)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        viewModel = LoginViewModel(),
        sharedViewModel = SharedViewModel()
    )
}
