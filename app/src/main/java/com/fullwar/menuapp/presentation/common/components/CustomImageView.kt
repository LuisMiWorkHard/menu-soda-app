package com.fullwar.menuapp.presentation.common.components

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
import com.fullwar.menuapp.ui.theme.CornerRadiusSmall
import com.fullwar.menuapp.ui.theme.HeavyGray

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
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
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
