package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.CambiarContrasenaRequestDto
import com.fullwar.menuapp.data.model.UsuarioResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class UsuarioService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "UsuarioService"
    }

    suspend fun getUsuario(): UsuarioResponseDto {
        val response = httpClient.get("api/usuario")
        Log.d(TAG, "getUsuario() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun cambiarContrasena(request: CambiarContrasenaRequestDto) {
        val response = httpClient.put("api/auth/contrasena") { setBody(request) }
        Log.d(TAG, "cambiarContrasena() - Status: ${response.status.value}")
    }
}
