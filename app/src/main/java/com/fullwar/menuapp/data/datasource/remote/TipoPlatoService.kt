package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TipoPlatoService(private val httpClient: HttpClient) {
    companion object {
        private const val TAG = "TipoPlatoService"
    }

    suspend fun getTiposPlato(): List<TipoPlatoResponseDto> {
        val response = httpClient.get("api/tipoplato")
        Log.d(TAG, "getTiposPlato() - Status: ${response.status.value}")
        return response.body()
    }
}
