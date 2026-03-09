package com.fullwar.menuapp.data.repository

import android.util.Log
import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val secureStorage: SecureStorageProvider,
    private val cookiesStorage: SecureCookiesStorageImpl
) : TokenProvider {

    companion object {
        private const val TAG = "AuthRepositoryImpl"
        private const val ACCESS_TOKEN_KEY = "access_token"
    }

    // Caché en memoria para acceso síncrono rápido
    private var cachedToken: String? = null

    /**
     * Inicializa el caché desde almacenamiento seguro.
     * Debe llamarse al inicio de la app.
     */
    suspend fun initialize() {
        cachedToken = secureStorage.getString(ACCESS_TOKEN_KEY)
        Log.d(TAG, "Token cache initialized: ${if (cachedToken != null) "token found" else "no token"}")
    }

    override fun getToken(): String? = cachedToken

    suspend fun loginAsync(credentials: LoginRequestDto): LoginResponseDto {
        val startTime = System.currentTimeMillis()

        val response = authService.login(credentials)
        cachedToken = response.accessToken

        // Guardar en almacenamiento seguro sin bloquear
        withContext(Dispatchers.IO) {
            secureStorage.putString(ACCESS_TOKEN_KEY, response.accessToken)
        }

        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "loginAsync completed in ${elapsed}ms")

        return response
    }

    suspend fun refreshAsync(): LoginResponseDto {
        val startTime = System.currentTimeMillis()

        val response = authService.refresh()
        cachedToken = response.accessToken

        withContext(Dispatchers.IO) {
            secureStorage.putString(ACCESS_TOKEN_KEY, response.accessToken)
        }

        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "refreshAsync completed in ${elapsed}ms")

        return response
    }

    suspend fun logoutAsync() {
        authService.logout()
        cachedToken = null

        withContext(Dispatchers.IO) {
            secureStorage.remove(ACCESS_TOKEN_KEY)
            cookiesStorage.clear()
        }

        Log.d(TAG, "logoutAsync completed")
    }
}
