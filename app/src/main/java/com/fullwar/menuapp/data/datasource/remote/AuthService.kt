package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

class AuthService(
    private val httpClient: HttpClient,
    private val locationProvider: LocationProvider
) {

    companion object {
        private const val TAG = "AuthService"
    }

    suspend fun login(credentials: LoginRequestDto): LoginResponseDto {
        val response = httpClient.post("api/auth/login") {
            locationProvider.getLatitude()?.let { header("GeoLat", it) }
            locationProvider.getLongitude()?.let { header("GeoLon", it) }
            setBody(credentials)
        }
        Log.d(TAG, "login() - Status: ${response.status.value}, Body: ${response.bodyAsText()}")
        return response.body()
    }

    suspend fun refresh(): LoginResponseDto {
        val response = httpClient.post("api/auth/refresh")
        Log.d(TAG, "refresh() - Status: ${response.status.value}, Body: ${response.bodyAsText()}")
        return response.body()
    }

    suspend fun logout() {
        val response = httpClient.post("api/auth/logout")
        Log.d(TAG, "logout() - Status: ${response.status.value}, Body: ${response.bodyAsText()}")
    }
}
