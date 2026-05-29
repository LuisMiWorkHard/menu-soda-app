package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.fullwar.menuapp.data.model.EnviarCodigoRecuperacionResponseDto
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.model.LoginResponseDto
import com.fullwar.menuapp.data.model.RestablecerContrasenaRecuperacionRequestDto
import com.fullwar.menuapp.data.model.VerificarCodigoRecuperacionRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
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

    suspend fun enviarCodigoRecuperacion(token: String): EnviarCodigoRecuperacionResponseDto {
        val response = httpClient.post("api/auth/recuperar/codigo") {
            header("Authorization", "Bearer $token")
        }
        Log.d(TAG, "enviarCodigoRecuperacion() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun verificarCodigoRecuperacion(token: String, request: VerificarCodigoRecuperacionRequestDto) {
        val response = httpClient.post("api/auth/recuperar/verificar") {
            header("Authorization", "Bearer $token")
            setBody(request)
        }
        Log.d(TAG, "verificarCodigoRecuperacion() - Status: ${response.status.value}")
    }

    suspend fun restablecerContrasenaRecuperacion(token: String, request: RestablecerContrasenaRecuperacionRequestDto) {
        val response = httpClient.put("api/auth/recuperar/contrasena") {
            header("Authorization", "Bearer $token")
            setBody(request)
        }
        Log.d(TAG, "restablecerContrasenaRecuperacion() - Status: ${response.status.value}")
    }
}
