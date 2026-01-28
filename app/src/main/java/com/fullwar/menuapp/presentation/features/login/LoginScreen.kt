package com.fullwar.menuapp.presentation.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.fullwar.menuapp.ui.theme.SodaGray
import com.fullwar.menuapp.ui.theme.SodaGrayLight
import com.fullwar.menuapp.ui.theme.SodaOrange
import com.fullwar.menuapp.ui.theme.SodaOrangeLight
import com.fullwar.menuapp.ui.theme.WhatsAppGreen

@Composable
fun LoginScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    var documentType by remember { mutableStateOf("DNI") }
    var documentNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Icon Header
        Surface(
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = SodaOrangeLight
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = SodaOrange,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title and Subtitle
        Text(
            text = "Welcome back!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Access your dashboard to share\ntoday's menu via WhatsApp.",
            fontSize = 16.sp,
            color = SodaGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Document Type Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Document Type",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            SegmentedControl(
                options = listOf("DNI", "RUC", "OTHER"),
                selectedOption = documentType,
                onOptionSelected = { documentType = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Document Number Field
        LoginTextField(
            label = "Document Number",
            value = documentNumber,
            onValueChange = { documentNumber = it },
            placeholder = "Enter your document number",
            leadingIcon = Icons.Filled.AccountBox
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password Field
        LoginTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "Enter your password",
            leadingIcon = null,
            trailingIcon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            onTrailingIconClick = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isPasswordField = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { /* TODO: Login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SodaOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WhatsApp Status
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = WhatsAppGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ready to share on WhatsApp", color = SodaGray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Row {
            Text(text = "Don't have an account? ", color = SodaGray)
            Text(text = "Sign up", color = SodaOrange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SodaGrayLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (isSelected) SodaOrange else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
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
    isPasswordField: Boolean = false
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
                fontSize = 16.sp,
                color = Color.Black
            )
            if (isPasswordField) {
                Text(
                    text = "Forgot?",
                    color = SodaOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = SodaGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SodaOrange,
                unfocusedBorderColor = SodaGrayLight.copy(alpha = 0.5f),
                cursorColor = SodaOrange
            ),
            trailingIcon = {
                if (trailingIcon != null) {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(imageVector = trailingIcon, contentDescription = null, tint = SodaGray)
                    }
                } else if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = SodaGray)
                }
            },
            visualTransformation = visualTransformation,
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
