package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class EntradaService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "EntradaService"
    }

    suspend fun getEntradas(filter: String? = null): List<EntradaResponseDto> {
        val response = httpClient.get("api/entrada") {
            filter?.takeIf { it.isNotBlank() }?.let { parameter("filter", it) }
        }
        Log.d(TAG, "getEntradas() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto {
        val response = httpClient.post("api/entrada") {
            setBody(request)
        }
        Log.d(TAG, "createEntrada() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun updateEntrada(id: Int, request: EntradaUpdateRequestDto): EntradaResponseDto {
        val response = httpClient.put("api/entrada/$id") {
            setBody(request)
        }
        Log.d(TAG, "updateEntrada() - Status: ${response.status.value}")
        return response.body()
    }
}
