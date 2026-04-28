package com.fullwar.menuapp.presentation.features.splash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    var progress by mutableFloatStateOf(0f)
        private set

    var currentPhraseIndex by mutableIntStateOf(0)
        private set

    var isInitialized by mutableStateOf(false)
        private set

    var hasValidToken by mutableStateOf(false)
        private set

    val phrases = listOf(
        "Organiza tu menú semanal en segundos.",
        "Personaliza tus platos y entradas favoritas.",
        "Crea menús visualmente atractivos.",
        "Gestiona tus categorías y fotos fácilmente.",
        "Tu cocina, siempre bajo control."
    )

    init {
        startSplashLogic()
    }

    private fun startSplashLogic() {
        // Rotación de frases cada 3 segundos
        viewModelScope.launch {
            while (!isInitialized) {
                delay(3000)
                currentPhraseIndex = (currentPhraseIndex + 1) % phrases.size
            }
        }

        // Simulación de progreso e inicialización real
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            
            // Inicialización real del repositorio
            authRepository.initialize()
            
            // Aseguramos que el splash dure al menos un tiempo mínimo para ser apreciado (ej. 2s)
            // o lo que demore la inicialización, lo que sea mayor.
            val minDuration = 2000L
            
            // Incremento gradual del progreso
            while (progress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                // Si ya inicializó, aceleramos el final
                val targetProgress = if (elapsed > minDuration) 1f else elapsed.toFloat() / minDuration
                
                progress = targetProgress.coerceAtMost(1f)
                delay(50)
            }

            hasValidToken = when {
                authRepository.getToken() == null -> false
                !authRepository.isCurrentTokenExpired() -> true
                else -> {
                    Log.d("SplashViewModel", "Token expirado, intentando refresh...")
                    try {
                        authRepository.refreshAsync()
                        Log.d("SplashViewModel", "Refresh exitoso, navegando a Home")
                        true
                    } catch (e: Exception) {
                        Log.w("SplashViewModel", "Refresh fallido, limpiando sesión: ${e.message}")
                        authRepository.clearLocalSession()
                        false
                    }
                }
            }
            isInitialized = true
        }
    }
}
