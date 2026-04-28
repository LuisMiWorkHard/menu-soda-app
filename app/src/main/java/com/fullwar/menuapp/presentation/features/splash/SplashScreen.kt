package com.fullwar.menuapp.presentation.features.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.DrawableImage
import com.fullwar.menuapp.R
import com.fullwar.menuapp.presentation.navigation.AppScreens
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel()
) {
    // Colores basados en la imagen
    val darkBackground = Color(0xFF1A1A1A)
    val accentGreen = Color(0xFF8BC34A)
    val cardBackground = Color(0xFFF5F5F0)

    // Redirección cuando termina la carga
    LaunchedEffect(viewModel.isInitialized) {
        if (viewModel.isInitialized) {
            val route = if (viewModel.hasValidToken) {
                AppScreens.HomeScreen.route
            } else {
                AppScreens.LoginScreen.route
            }
            navController.navigate(route) {
                popUpTo(AppScreens.SplashScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        // Contenido Central
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_splash),
                contentDescription = "App Logo",
                modifier = Modifier.fillMaxWidth(0.65f),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Tu cocina,",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "organizada",
                color = accentGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Barra de progreso
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = { viewModel.progress },
                    modifier = Modifier
                        .width(200.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentGreen,
                    trackColor = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Cargando...",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        // Tarjeta Inferior con Frases
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = cardBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.RoomService,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = darkBackground
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = viewModel.currentPhraseIndex,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) with
                        fadeOut(animationSpec = tween(500)) using
                        SizeTransform(clip = false)
                    },
                    label = "PhraseAnimation"
                ) { index ->
                    Text(
                        text = viewModel.phrases[index],
                        color = darkBackground,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}
