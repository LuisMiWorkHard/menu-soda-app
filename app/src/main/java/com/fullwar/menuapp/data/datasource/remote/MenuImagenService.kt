package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MenuImagenService(private val httpClient: HttpClient) {
    companion object {
        private const val TAG = "MenuImagenService"
    }

    suspend fun getMenuImagenes(): List<MenuImagenResponseDto> {
        val response = httpClient.get("api/menuimagen")
        Log.d(TAG, "getMenuImagenes() - Status: ${response.status.value}")
        return response.body()
    }
}
