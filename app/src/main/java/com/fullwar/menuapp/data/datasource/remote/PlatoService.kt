package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class PlatoService(private val httpClient: HttpClient) {
    companion object {
        private const val TAG = "PlatoService"
    }

    suspend fun getPlatos(): List<PlatoResponseDto> {
        val response = httpClient.get("api/plato")
        Log.d(TAG, "getPlatos() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto {
        val response = httpClient.post("api/plato") {
            setBody(request)
        }
        Log.d(TAG, "createPlato() - Status: ${response.status.value}")
        return response.body()
    }
}
