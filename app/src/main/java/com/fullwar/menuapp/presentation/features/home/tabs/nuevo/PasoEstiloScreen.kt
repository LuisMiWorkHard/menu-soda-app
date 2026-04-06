package com.fullwar.menuapp.presentation.features.home.tabs.nuevo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.TextSizeLarge

@Composable
fun PasoEstiloScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.nuevo_proximamente),
            fontSize = TextSizeLarge,
            fontWeight = FontWeight.Medium,
            color = HeavyGray
        )
    }
}
