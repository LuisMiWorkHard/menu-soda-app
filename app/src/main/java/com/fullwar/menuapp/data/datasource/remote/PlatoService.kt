package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.PlatoUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PlatoService(private val httpClient: HttpClient) {
    companion object {
        private const val TAG = "PlatoService"
    }

    suspend fun getPlatos(nombre: String? = null): List<PlatoResponseDto> {
        val response = httpClient.get("api/plato") {
            nombre?.takeIf { it.isNotBlank() }?.let { parameter("nombre", it) }
        }
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

    suspend fun updatePlato(id: Int, request: PlatoUpdateRequestDto) {
        val response = httpClient.put("api/plato/$id") {
            setBody(request)
        }
        Log.d(TAG, "updatePlato() - Status: ${response.status.value}")
    }
}
