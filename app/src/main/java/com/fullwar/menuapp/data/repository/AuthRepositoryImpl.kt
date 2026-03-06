package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto
import com.liftric.kvault.KVault

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val kVault: KVault
) : TokenProvider {

    // Caché en memoria, inicializado desde disco cifrado
    private var cachedToken: String? = kVault.string(ACCESS_TOKEN_KEY)

    override fun getToken(): String? = cachedToken

    suspend fun loginAsync(credentials: LoginRequestDto): LoginResponseDto {
        val response = authService.login(credentials)
        cachedToken = response.accessToken
        kVault.set(ACCESS_TOKEN_KEY, response.accessToken)
        kVault.set(REFRESH_TOKEN_KEY, response.refreshToken)
        return response
    }

    suspend fun refreshAsync(): LoginResponseDto {
        val response = authService.refresh()
        cachedToken = response.accessToken
        kVault.set(ACCESS_TOKEN_KEY, response.accessToken)
        kVault.set(REFRESH_TOKEN_KEY, response.refreshToken)
        return response
    }

    suspend fun logoutAsync() {
        authService.logout()
        cachedToken = null
        kVault.deleteObject(ACCESS_TOKEN_KEY)
        kVault.deleteObject(REFRESH_TOKEN_KEY)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
