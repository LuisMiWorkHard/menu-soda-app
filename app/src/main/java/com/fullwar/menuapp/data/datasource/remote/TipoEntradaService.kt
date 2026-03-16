package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TipoEntradaService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "TipoEntradaService"
    }

    suspend fun getTiposEntrada(): List<TipoEntradaResponseDto> {
        val response = httpClient.get("api/tipoentrada")
        Log.d(TAG, "getTiposEntrada() - Status: ${response.status.value}")
        return response.body()
    }
}
