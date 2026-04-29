package com.fullwar.menuapp.presentation.features.splash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    var isInitialized by mutableStateOf(false)
        private set

    var hasValidToken by mutableStateOf(false)
        private set

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            authRepository.initialize()

            hasValidToken = when {
                authRepository.getToken() == null -> false
                !authRepository.isCurrentTokenExpired() -> true
                else -> {
                    Log.d("SplashViewModel", "Token expirado, intentando refresh...")
                    try {
                        authRepository.refreshAsync()
                        Log.d("SplashViewModel", "Refresh exitoso")
                        true
                    } catch (e: Exception) {
                        Log.w("SplashViewModel", "Refresh fallido: ${e.message}")
                        authRepository.clearLocalSession()
                        false
                    }
                }
            }

            isInitialized = true
        }
    }
}
