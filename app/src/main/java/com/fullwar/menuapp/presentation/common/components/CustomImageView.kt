package com.fullwar.menuapp.presentation.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.StrokeWidthMedium

@Composable
fun CustomImageView(
    imageUrl: String?,
    sizeDp: Int = 60,
    heightDp: Int = sizeDp,
    @DrawableRes defaultImageRes: Int = R.drawable.default_image_meal,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(CornerRadiusSmall)
    val context = LocalContext.current
    val pxW = with(LocalDensity.current) { sizeDp.dp.toPx() }.toInt()
    val pxH = with(LocalDensity.current) { heightDp.dp.toPx() }.toInt()

    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(pxW, pxH)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier
                .width(sizeDp.dp)
                .height(heightDp.dp)
                .clip(shape)
                .background(HeavyGray, shape),
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HeavyGray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(IconSizeMedium),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = StrokeWidthMedium
                    )
                }
            },
            error = {
                Image(
                    painter = painterResource(id = defaultImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(sizeDp.dp)
                        .height(heightDp.dp)
                        .clip(shape)
                )
            }
        )
    } else {
        Image(
            painter = painterResource(id = defaultImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .width(sizeDp.dp)
                .height(heightDp.dp)
                .clip(shape)
                .background(HeavyGray, shape)
        )
    }
}

@Preview(showBackground = true, name = "CustomImageView - Sin URL - Claro")
@Composable
private fun CustomImageViewSinUrlClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        CustomImageView(imageUrl = null)
    }
}

@Preview(showBackground = true, name = "CustomImageView - Sin URL - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CustomImageViewSinUrlOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CustomImageView(imageUrl = null)
        }
    }
}

@Preview(showBackground = true, name = "CustomImageView - Con URL - Claro")
@Composable
private fun CustomImageViewConUrlClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        CustomImageView(imageUrl = "https://example.com/imagen.jpg")
    }
}

@Preview(showBackground = true, name = "CustomImageView - Con URL - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CustomImageViewConUrlOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CustomImageView(imageUrl = "https://example.com/imagen.jpg")
        }
    }
}

@Preview(showBackground = true, name = "CustomImageView - Rectangular - Claro")
@Composable
private fun CustomImageViewRectangularClaroPreview() {
    MenuAppTheme(darkTheme = false) {
        CustomImageView(imageUrl = null, sizeDp = 120, heightDp = 60)
    }
}

@Preview(showBackground = true, name = "CustomImageView - Rectangular - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CustomImageViewRectangularOscuroPreview() {
    MenuAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CustomImageView(imageUrl = null, sizeDp = 120, heightDp = 60)
        }
    }
}
