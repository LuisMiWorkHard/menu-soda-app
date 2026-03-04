package com.fullwar.menuapp.data.datasource.remote

import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthService(private val httpClient: HttpClient) {

    suspend fun login(credentials: LoginRequestDto): LoginResponseDto {
        return httpClient.post("api/auth/login") {
            setBody(credentials)
        }.body()
    }

    suspend fun refresh(): LoginResponseDto {
        return httpClient.post("api/auth/refresh").body()
    }

    suspend fun logout() {
        httpClient.post("api/auth/logout")
    }
}
