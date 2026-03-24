package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class EntradaService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "EntradaService"
    }

    suspend fun getEntradas(): List<EntradaResponseDto> {
        val response = httpClient.get("api/entrada")
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
}
