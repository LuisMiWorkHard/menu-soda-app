package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.CornerRadiusXLarge
import com.fullwar.menuapp.ui.theme.CornerRadiusXXSmall
import com.fullwar.menuapp.ui.theme.ElevationSmall
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.IconSizeXLarge
import com.fullwar.menuapp.ui.theme.ImageSizeMedium
import com.fullwar.menuapp.ui.theme.ImageSizeXLarge
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.StrokeWidthMedium
import com.fullwar.menuapp.ui.theme.StrokeWidthThick
import com.fullwar.menuapp.ui.theme.TextSizeMedium
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import com.fullwar.menuapp.ui.theme.TextSizeXSmall

@Composable
internal fun FotoOrigenDialogContent(
    onDismissRequest: () -> Unit,
    onCamara: () -> Unit,
    onGaleria: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(CornerRadiusXLarge),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = ElevationSmall
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono principal con destellos decorativos
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 2.dp, top = 16.dp),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                )
                Text(
                    text = "✦",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 8.dp),
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                )
                Text(
                    text = "✦",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 6.dp, bottom = 10.dp),
                    fontSize = 7.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = "✦",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 2.dp, bottom = 14.dp),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                Box(
                    modifier = Modifier
                        .size(ImageSizeXLarge)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSizeXLarge)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMedium))

            Text(
                text = stringResource(R.string.foto_origen_pregunta),
                fontWeight = FontWeight.Bold,
                fontSize = TextSizeXLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            // Línea separadora
            Box(
                modifier = Modifier
                    .width(IconSizeXLarge)
                    .height(StrokeWidthThick)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(CornerRadiusXXSmall)
                    )
            )

            Spacer(modifier = Modifier.height(SpacingLarge))

            FotoOrigenOpcionItem(
                icon = Icons.Outlined.PhotoLibrary,
                iconBgColor = MaterialTheme.colorScheme.primary,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                titulo = stringResource(R.string.foto_origen_galeria),
                subtitulo = stringResource(R.string.foto_origen_galeria_subtitulo),
                onClick = {
                    onDismissRequest()
                    onGaleria()
                }
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            FotoOrigenOpcionItem(
                icon = Icons.Outlined.PhotoCamera,
                iconBgColor = MaterialTheme.colorScheme.secondary,
                iconTint = MaterialTheme.colorScheme.onSecondary,
                titulo = stringResource(R.string.foto_origen_camara),
                subtitulo = stringResource(R.string.foto_origen_camara_subtitulo),
                onClick = {
                    onDismissRequest()
                    onCamara()
                }
            )

            Spacer(modifier = Modifier.height(SpacingSmall))

            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.calendar_cancel),
                    color = HeavyGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = TextSizeMedium
                )
            }

            Spacer(modifier = Modifier.height(SpacingSmall))
        }
    }
}

@Composable
fun FotoOrigenDialog(
    onDismissRequest: () -> Unit,
    onCamara: () -> Unit,
    onGaleria: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FotoOrigenDialogContent(
            onDismissRequest = onDismissRequest,
            onCamara = onCamara,
            onGaleria = onGaleria,
        )
    }
}

@Composable
private fun FotoOrigenOpcionItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp // elevación explícita en cero
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ImageSizeMedium)
                    .background(iconBgColor, RoundedCornerShape(CornerRadiusMedium)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(IconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.width(SpacingMedium))

            Column {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextSizeMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(StrokeWidthMedium))
                Text(
                    text = subtitulo,
                    fontSize = TextSizeXSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "FotoOrigenDialog - Claro")
@Preview(showBackground = true, name = "FotoOrigenDialog - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewFotoOrigenDialog() {
    MenuAppTheme {
        FotoOrigenDialogContent(
            onDismissRequest = {},
            onCamara = {},
            onGaleria = {}
        )
    }
}
