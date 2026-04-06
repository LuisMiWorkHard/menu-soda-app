package com.fullwar.menuapp.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fullwar.menuapp.R
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fullwar.menuapp.di.Constants
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.HeavyGray

@Composable
fun CustomImageView(
    imagenId: Int?,
    sizeDp: Int = 60,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(CornerRadiusSmall)
    val hasImage = imagenId != null && imagenId > 0
    val imageUrl = if (hasImage) "${Constants.BASE_URL}/api/imagen/$imagenId/contenido" else null
    val context = LocalContext.current
    val px = with(LocalDensity.current) { sizeDp.dp.toPx() }.toInt()

    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(px, px)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = modifier
                .size(sizeDp.dp)
                .clip(shape),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HeavyGray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HeavyGray)
                )
            }
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.default_image_meal),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(sizeDp.dp)
                .clip(shape)
                .background(HeavyGray, shape)
        )
    }
}
