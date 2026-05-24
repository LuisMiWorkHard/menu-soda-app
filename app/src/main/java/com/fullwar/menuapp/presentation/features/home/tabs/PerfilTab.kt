package com.fullwar.menuapp.presentation.features.home.tabs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.fullwar.menuapp.R
import com.fullwar.menuapp.ui.theme.MenuAppTheme

@Composable
fun PerfilTab(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.tab_perfil_placeholder))
    }
}

@Preview(showBackground = true, name = "PerfilTab - Claro")
@Preview(showBackground = true, name = "PerfilTab - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PerfilTabPreview() {
    MenuAppTheme {
        PerfilTab()
    }
}
