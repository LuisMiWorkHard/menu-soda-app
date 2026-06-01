package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.fullwar.menuapp.data.model.EnviarCodigoRecuperacionResponseDto
import com.fullwar.menuapp.data.model.RestablecerContrasenaRecuperacionRequestDto
import com.fullwar.menuapp.data.model.VerificarCodigoRecuperacionRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class RecuperarContrasenaService(
    private val httpClient: HttpClient,
    private val locationProvider: LocationProvider
) {

    companion object {
        private const val TAG = "RecuperarContrasenaService"
    }

    suspend fun enviarCodigoRecuperacion(): EnviarCodigoRecuperacionResponseDto {
        val response = httpClient.post("api/auth/recuperar/codigo") {
            locationProvider.getLatitude()?.let { header("GeoLat", it) }
            locationProvider.getLongitude()?.let { header("GeoLon", it) }
        }
        Log.d(TAG, "enviarCodigoRecuperacion() - Status: ${response.status.value}")
        return response.body()
    }

    suspend fun verificarCodigoRecuperacion(request: VerificarCodigoRecuperacionRequestDto) {
        val response = httpClient.post("api/auth/recuperar/verificar") {
            locationProvider.getLatitude()?.let { header("GeoLat", it) }
            locationProvider.getLongitude()?.let { header("GeoLon", it) }
            setBody(request)
        }
        Log.d(TAG, "verificarCodigoRecuperacion() - Status: ${response.status.value}")
    }

    suspend fun restablecerContrasenaRecuperacion(request: RestablecerContrasenaRecuperacionRequestDto) {
        val response = httpClient.put("api/auth/recuperar/contrasena") {
            locationProvider.getLatitude()?.let { header("GeoLat", it) }
            locationProvider.getLongitude()?.let { header("GeoLon", it) }
            setBody(request)
        }
        Log.d(TAG, "restablecerContrasenaRecuperacion() - Status: ${response.status.value}")
    }
}
