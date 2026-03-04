package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.local.TokenProvider
import com.fullwar.menuapp.data.datasource.remote.AuthService
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto

class AuthRepositoryImpl(
    private val authService: AuthService
) : TokenProvider {

    private var jwtToken: String? = null

    override fun getToken(): String? = jwtToken

    suspend fun loginAsync(credentials: LoginRequestDto): LoginResponseDto {
        val response = authService.login(credentials)
        jwtToken = response.accessToken
        return response
    }

    suspend fun refreshAsync(): LoginResponseDto {
        val response = authService.refresh()
        jwtToken = response.accessToken
        return response
    }

    suspend fun logoutAsync() {
        authService.logout()
        jwtToken = null
    }
}
