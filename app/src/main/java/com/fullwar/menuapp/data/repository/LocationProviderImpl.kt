package com.fullwar.menuapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationProviderImpl(
    context: Context
) : LocationProvider {

    companion object {
        private const val TAG = "LocationProviderImpl"
    }

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var cachedLatitude: String? = null
    private var cachedLongitude: String? = null

    override fun getLatitude(): String? = cachedLatitude

    override fun getLongitude(): String? = cachedLongitude

    /**
     * Solicita la ubicación actual del dispositivo y actualiza el caché interno.
     * Debe llamarse después de que el usuario haya concedido los permisos de ubicación.
     */
    @SuppressLint("MissingPermission")
    override suspend fun actualizarUbicacion(permisosConcedidos: Boolean) {
        if (!permisosConcedidos) {
            Log.w(TAG, "No se tienen permisos de ubicación, no se puede obtener la ubicación")
            return
        }

        try {
            val locationResult = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }

            if (locationResult != null) {
                cachedLatitude = locationResult.latitude.toString()
                cachedLongitude = locationResult.longitude.toString()
                Log.d(TAG, "Ubicación obtenida: lat=$cachedLatitude, lon=$cachedLongitude")
            } else {
                Log.w(TAG, "getCurrentLocation devolvió null, intentando con última conocida")
                obtenerUltimaUbicacionConocida()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ubicación actual: ${e.message}", e)
            obtenerUltimaUbicacionConocida()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun obtenerUltimaUbicacionConocida() {
        try {
            val lastLocation = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            if (lastLocation != null) {
                cachedLatitude = lastLocation.latitude.toString()
                cachedLongitude = lastLocation.longitude.toString()
                Log.d(TAG, "Última ubicación conocida: lat=$cachedLatitude, lon=$cachedLongitude")
            } else {
                Log.w(TAG, "No hay ubicación disponible (lastLocation es null)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener última ubicación: ${e.message}", e)
        }
    }
}

