package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.PerfilResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PerfilService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "PerfilService"
    }

    suspend fun getPerfil(): PerfilResponseDto {
        val response = httpClient.get("api/perfil")
        Log.d(TAG, "getPerfil() - Status: ${response.status.value}")
        return response.body()
    }
}
