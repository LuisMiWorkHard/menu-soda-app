package com.fullwar.menuapp.presentation.features.home.tabs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.PerfilResponseDto
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.components.MenuSodaDialog
import com.fullwar.menuapp.presentation.common.components.MenuSodaDialogVariant
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.presentation.features.home.tabs.perfil.PerfilViewModel
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.DangerRed
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSize2XLarge
import com.fullwar.menuapp.ui.theme.IconSizeLarge
import com.fullwar.menuapp.ui.theme.IconSizeMedium
import com.fullwar.menuapp.ui.theme.IconSize3XLarge
import com.fullwar.menuapp.ui.theme.IconSizeXLarge
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.Spacing3XLarge
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXLarge
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXLarge
import com.fullwar.menuapp.ui.theme.TextSizeXXLarge
import com.fullwar.menuapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun PerfilTab(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    onPerfilUsuarioClick: () -> Unit = {},
    onContrasenaClick: () -> Unit = {},
    viewModel: PerfilViewModel = koinViewModel()
) {
    LifecycleResumeEffect(Unit) {
        viewModel.loadPerfil()
        onPauseOrDispose { }
    }

    PerfilTabContent(
        modifier = modifier,
        perfilState = viewModel.perfilState,
        onRetry = viewModel::loadPerfil,
        onPerfilUsuarioClick = onPerfilUsuarioClick,
        onContrasenaClick = onContrasenaClick,
        onCerrarSesion = { viewModel.cerrarSesion(onLogout) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerfilTabContent(
    modifier: Modifier = Modifier,
    perfilState: State<PerfilResponseDto>,
    onRetry: () -> Unit = {},
    onPerfilUsuarioClick: () -> Unit = {},
    onContrasenaClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var showConfirmCerrarSesion by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,

    ) { innerPadding ->
        when (perfilState) {
            is State.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(top = Spacing3XLarge)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = SpacingLarge)
                ) {
                    PerfilCabecera(perfil = perfilState.data)

                    Spacer(modifier = Modifier.height(SpacingXLarge))

                    PerfilOpcionCard(
                        icon = Icons.Filled.Person,
                        useAvatarCircle = true,
                        title = stringResource(id = R.string.perfil_usuario_titulo),
                        subtitle = stringResource(id = R.string.perfil_usuario_subtitulo),
                        onClick = onPerfilUsuarioClick
                    )

                    Spacer(modifier = Modifier.height(SpacingMedium))

                    PerfilOpcionCard(
                        icon = Icons.Filled.Lock,
                        useAvatarCircle = false,
                        title = stringResource(id = R.string.perfil_password_titulo),
                        subtitle = stringResource(id = R.string.perfil_password_subtitulo),
                        onClick = onContrasenaClick
                    )

                    Spacer(modifier = Modifier.height(SpacingXLarge))

                    PerfilCerrarSesionCard(onClick = { showConfirmCerrarSesion = true })
                }
            }

            is State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = SpacingLarge),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorBanner(
                        message = perfilState.message,
                        onRetry = onRetry
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }

    if (showConfirmCerrarSesion) {
        MenuSodaDialog(
            title = stringResource(id = R.string.perfil_cerrar_sesion),
            message = stringResource(id = R.string.perfil_cerrar_sesion_mensaje),
            onDismissRequest = { showConfirmCerrarSesion = false },
            confirmLabel = stringResource(id = R.string.perfil_cerrar_sesion),
            onConfirm = {
                showConfirmCerrarSesion = false
                onCerrarSesion()
            },
            dismissLabel = stringResource(id = R.string.calendar_cancel),
            onDismiss = { showConfirmCerrarSesion = false },
            icon = Icons.AutoMirrored.Filled.Logout,
            variant = MenuSodaDialogVariant.Warning
        )
    }
}

@Composable
private fun PerfilCabecera(perfil: PerfilResponseDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SpacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.perfil_saludo, perfil.nombreCompleto),
            fontWeight = FontWeight.Bold,
            fontSize = TextSizeXXLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(SpacingXSmall))
        Text(
            text = perfil.email,
            fontSize = TextSizeSmall,
            color = HeavyGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PerfilOpcionCard(
    icon: ImageVector,
    useAvatarCircle: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(SpacingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(IconSize2XLarge)
                    .padding(SpacingSmall)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = SpacingMedium)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = TextSizeLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = TextSizeSmall,
                    color = HeavyGray
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = HeavyGray,
                modifier = Modifier.size(IconSizeMedium)
            )
        }
    }
}

@Composable
private fun PerfilCerrarSesionCard(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(SpacingLarge),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMedium)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier
                    .size(IconSize2XLarge)
                    .padding(SpacingSmall)
            )
            Text(
                text = stringResource(id = R.string.perfil_cerrar_sesion),
                fontWeight = FontWeight.SemiBold,
                fontSize = TextSizeLarge,
                color = DangerRed
            )
        }
    }
}

@Preview(showBackground = true, name = "PerfilTab - Claro")
@Preview(showBackground = true, name = "PerfilTab - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PerfilTabPreview() {
    MenuAppTheme {
        PerfilTabContent(
            perfilState = State.Success(
                PerfilResponseDto(
                    nombreCompleto = "Juan Pérez",
                    email = "juan.perez@email.com"
                )
            )
        )
    }
}
