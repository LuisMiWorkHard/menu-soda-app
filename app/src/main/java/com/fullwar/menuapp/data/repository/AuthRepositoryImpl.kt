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

    fun isCurrentTokenExpired(): Boolean {
        val token = cachedToken ?: return true
        return isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payload = String(
                android.util.Base64.decode(
                    parts[1],
                    android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING
                ),
                Charsets.UTF_8
            )
            val exp = Regex(""""exp"\s*:\s*(\d+)""")
                .find(payload)?.groupValues?.get(1)?.toLongOrNull() ?: return true
            System.currentTimeMillis() / 1000L >= (exp - 30L)
        } catch (e: Exception) {
            Log.w(TAG, "isTokenExpired: error al decodificar JWT, se trata como expirado")
            true
        }
    }

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

    suspend fun clearLocalSession() {
        cachedToken = null
        withContext(Dispatchers.IO) {
            secureStorage.remove(ACCESS_TOKEN_KEY)
            cookiesStorage.clear()
        }
        Log.d(TAG, "clearLocalSession completed")
    }

    suspend fun logoutAsync() {
        try {
            authService.logout()
            Log.d(TAG, "logoutAsync: server logout successful")
        } catch (e: Exception) {
            Log.w(TAG, "logoutAsync: server logout failed (ignorado): ${e.message}")
        }
        cachedToken = null
        withContext(Dispatchers.IO) {
            secureStorage.remove(ACCESS_TOKEN_KEY)
            cookiesStorage.clear()
        }
        Log.d(TAG, "logoutAsync completed")
    }
}
