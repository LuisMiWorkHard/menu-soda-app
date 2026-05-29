package com.fullwar.menuapp.presentation.features.home.tabs.perfil.informacion_personal

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.UsuarioResponseDto
import com.fullwar.menuapp.presentation.common.components.ErrorBanner
import com.fullwar.menuapp.presentation.common.utils.State
import com.fullwar.menuapp.ui.theme.CornerRadiusMedium
import com.fullwar.menuapp.ui.theme.HeavyGray
import com.fullwar.menuapp.ui.theme.IconSize2XLarge
import com.fullwar.menuapp.ui.theme.MenuAppTheme
import com.fullwar.menuapp.ui.theme.SpacingLarge
import com.fullwar.menuapp.ui.theme.SpacingMedium
import com.fullwar.menuapp.ui.theme.SpacingSmall
import com.fullwar.menuapp.ui.theme.SpacingXSmall
import com.fullwar.menuapp.ui.theme.TextSizeLarge
import com.fullwar.menuapp.ui.theme.TextSizeSmall
import com.fullwar.menuapp.ui.theme.TextSizeXXLarge
import org.koin.androidx.compose.koinViewModel

@Composable
fun InformacionPersonalScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: InformacionPersonalViewModel = koinViewModel()
) {
    LifecycleResumeEffect(Unit) {
        viewModel.loadUsuario()
        onPauseOrDispose { }
    }

    InformacionPersonalContent(
        modifier = modifier,
        usuarioState = viewModel.usuarioState,
        onRetry = viewModel::loadUsuario,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InformacionPersonalContent(
    modifier: Modifier = Modifier,
    usuarioState: State<UsuarioResponseDto>,
    onRetry: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.informacion_personal_titulo),
                        fontWeight = FontWeight.Bold,
                        fontSize = TextSizeXXLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.informacion_personal_volver),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when (usuarioState) {
            is State.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = SpacingLarge)
                        .padding(bottom = SpacingLarge)
                ) {
                    val dto = usuarioState.data

                    InfoPersonalItem(
                        icon = Icons.Filled.Badge,
                        title = stringResource(id = R.string.informacion_personal_nombre),
                        value = dto.nombreCompleto.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    InfoPersonalItem(
                        icon = Icons.Filled.CreditCard,
                        title = stringResource(id = R.string.informacion_personal_documento),
                        value = dto.documento.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    InfoPersonalItem(
                        icon = Icons.Filled.Person,
                        title = stringResource(id = R.string.informacion_personal_genero),
                        value = dto.genero.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    InfoPersonalItem(
                        icon = Icons.Filled.Email,
                        title = stringResource(id = R.string.informacion_personal_correo),
                        value = dto.email.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    InfoPersonalItem(
                        icon = Icons.Filled.Phone,
                        title = stringResource(id = R.string.informacion_personal_telefono),
                        value = dto.telefono.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    InfoPersonalItem(
                        icon = Icons.Filled.Cake,
                        title = stringResource(id = R.string.informacion_personal_fecha_nac),
                        value = dto.fechaNacimiento.ifBlank { null }
                    )
                    Spacer(modifier = Modifier.height(SpacingXSmall))

                    if (dto.direccionCasa != null) {
                        InfoPersonalItem(
                            icon = Icons.Filled.Home,
                            title = stringResource(id = R.string.informacion_personal_dir_casa),
                            value = dto.direccionCasa
                        )
                        Spacer(modifier = Modifier.height(SpacingXSmall))
                    }
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
                        message = usuarioState.message,
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
}

@Composable
private fun InfoPersonalItem(
    icon: ImageVector,
    title: String,
    value: String?
) {
    Surface(
        shape = RoundedCornerShape(CornerRadiusMedium),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
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
                Spacer(modifier = Modifier.height(SpacingXSmall))
                Text(
                    text = value ?: stringResource(id = R.string.sin_establecer),
                    fontSize = TextSizeSmall,
                    color = if (value != null) MaterialTheme.colorScheme.onSurface else HeavyGray
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "InformacionPersonal - Claro")
@Preview(showBackground = true, name = "InformacionPersonal - Oscuro", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun InformacionPersonalPreview() {
    MenuAppTheme {
        InformacionPersonalContent(
            usuarioState = State.Success(
                UsuarioResponseDto(
                    nombreCompleto = "Luis Miguel Valeriano Vega",
                    documento = "DNI 12345678",
                    email = "luisvaleriano1009@gmail.com",
                    telefono = "974749260",
                    genero = "Masculino",
                    fechaNacimiento = "10 de septiembre de 1990",
                    direccionCasa = "Jirón San Martín 647, San Miguel"
                )
            )
        )
    }
}
