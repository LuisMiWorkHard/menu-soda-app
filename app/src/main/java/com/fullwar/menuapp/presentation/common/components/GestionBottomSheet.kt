package com.fullwar.menuapp.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.ui.theme.*

@Composable
internal fun GestionBottomSheetContent(
    title: String,
    saveLabel: String,
    cancelLabel: String,
    isLoading: Boolean,
    errorMessage: String?,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = SpacingXLarge)
            .padding(bottom = SpacingXXLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeXLarge
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = HeavyGray
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingLarge))

        content()

        Spacer(modifier = Modifier.height(SpacingXXLarge))

        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFCE4EC),
                shape = RoundedCornerShape(CornerRadiusSmall),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SpacingLarge)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = TextSizeSmall,
                    modifier = Modifier.padding(SpacingMedium)
                )
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonHeightLarge),
            shape = RoundedCornerShape(CornerRadiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(IconSizeMedium),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = saveLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingMedium))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = cancelLabel,
                color = HeavyGray,
                fontWeight = FontWeight.Medium,
                fontSize = TextSizeMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBottomSheet(
    title: String,
    saveLabel: String,
    cancelLabel: String,
    isLoading: Boolean,
    errorMessage: String?,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = CornerRadiusLarge, topEnd = CornerRadiusLarge)
    ) {
        GestionBottomSheetContent(
            title = title,
            saveLabel = saveLabel,
            cancelLabel = cancelLabel,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onSave = onSave,
            onDismiss = onDismiss,
            content = content
        )
    }
}
