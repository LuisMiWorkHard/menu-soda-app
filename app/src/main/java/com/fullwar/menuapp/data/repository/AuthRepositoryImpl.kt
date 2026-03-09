package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val secureStorage: SecureStorageProvider,
    private val cookiesStorage: SecureCookiesStorageImpl
) : TokenProvider {

    // Caché en memoria, inicializado desde disco cifrado
    private var cachedToken: String? = secureStorage.getString(ACCESS_TOKEN_KEY)

    override fun getToken(): String? = cachedToken

    suspend fun loginAsync(credentials: LoginRequestDto): LoginResponseDto {
        val response = authService.login(credentials)
        cachedToken = response.accessToken
        secureStorage.putString(ACCESS_TOKEN_KEY, response.accessToken)
        return response
    }

    suspend fun refreshAsync(): LoginResponseDto {
        val response = authService.refresh()
        cachedToken = response.accessToken
        secureStorage.putString(ACCESS_TOKEN_KEY, response.accessToken)
        return response
    }

    suspend fun logoutAsync() {
        authService.logout()
        cachedToken = null
        secureStorage.remove(ACCESS_TOKEN_KEY)
        cookiesStorage.clear()
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
}
