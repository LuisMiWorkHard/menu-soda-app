package com.fullwar.menuapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.fullwar.menuapp.data.datasource.local.LocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementación de LocationProvider usando estrategia híbrida optimizada:
 *
 * ESTRATEGIA:
 * 1. Intenta lastLocation primero (10-50ms) - Ubicación cacheada del sistema
 * 2. Si existe, usa inmediatamente y actualiza en background
 * 3. Si no existe, obtiene ubicación actual con GPS (500ms-2s)
 *
 * FLUJO PRIMERA VEZ:
 * - lastLocation → NULL (no hay caché)
 * - getCurrentLocation(HIGH_ACCURACY) → 500ms-2s
 * - Ubicación: REAL ACTUAL del GPS ✅
 *
 * FLUJO SUBSECUENTE:
 * - lastLocation → Ubicación cacheada (10-50ms) ⚡
 * - Login procede INMEDIATAMENTE
 * - Actualiza en background para próxima vez
 * - Ubicación: CACHEADA del sistema (30s-10min antigüedad)
 *
 * COMPATIBLE CON KMP:
 * - Android: FusedLocationProvider
 * - iOS: CoreLocation (locationManager.location + requestLocation)
 */
class LocationProviderImpl(
    context: Context
) : LocationProvider {

    companion object {
        private const val TAG = "LocationProviderImpl"
    }

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var cachedLatitude: String? = null
    private var cachedLongitude: String? = null

    override fun getLatitude(): String? = cachedLatitude

    override fun getLongitude(): String? = cachedLongitude

    /**
     * ESTRATEGIA HÍBRIDA: lastLocation (rápido) + getCurrentLocation (preciso)
     *
     * Primera vez: 500ms-2s (GPS real)
     * Subsecuente: 10-50ms (caché del sistema) ⚡
     */
    @SuppressLint("MissingPermission")
    override suspend fun actualizarUbicacion(permisosConcedidos: Boolean) {
        if (!permisosConcedidos) {
            Log.w(TAG, "No se tienen permisos de ubicación")
            return
        }

        val startTime = System.currentTimeMillis()

        try {
            // PASO 1: Intentar lastLocation primero (RÁPIDO: 10-50ms)
            Log.d(TAG, "Intentando lastLocation...")
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
                // ✅ Usar ubicación cacheada INMEDIATAMENTE
                cachedLatitude = lastLocation.latitude.toString()
                cachedLongitude = lastLocation.longitude.toString()

                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "✅ lastLocation obtenida en ${elapsed}ms (CACHEADA del sistema)")
                Log.d(TAG, "   Coordenadas: lat=$cachedLatitude, lon=$cachedLongitude")
                Log.d(TAG, "   Precisión: ±${lastLocation.accuracy.toInt()}m")
                Log.d(TAG, "   🔄 Actualizando en background para próxima vez...")

                // PASO 2: Actualizar en background para próximos logins
                actualizarUbicacionEnBackground()
                return
            }

            // PASO 3: Si no hay lastLocation, obtener ubicación actual
            val elapsed = System.currentTimeMillis() - startTime
            Log.d(TAG, "⚠️ No hay lastLocation (${elapsed}ms), obteniendo ubicación REAL con GPS...")
            obtenerUbicacionActual()

            val totalElapsed = System.currentTimeMillis() - startTime
            Log.d(TAG, "✅ Ubicación REAL obtenida en ${totalElapsed}ms")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo lastLocation: ${e.message}")
            // Fallback: obtener ubicación actual
            try {
                obtenerUbicacionActual()
                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "✅ Ubicación obtenida (fallback) en ${elapsed}ms")
            } catch (e2: Exception) {
                Log.e(TAG, "❌ Error en fallback: ${e2.message}", e2)
            }
        }
    }

    /**
     * Obtiene la ubicación REAL ACTUAL con GPS + WiFi + Cell Towers
     * Tiempo esperado: 500ms-2s
     * Prioridad: HIGH_ACCURACY (±5-10m)
     */
    @SuppressLint("MissingPermission")
    private suspend fun obtenerUbicacionActual() {
        val startTime = System.currentTimeMillis()

        val locationResult = suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,  // ⚡ Más rápido y preciso: 500ms-2s
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

            val elapsed = System.currentTimeMillis() - startTime
            Log.d(TAG, "📍 getCurrentLocation(HIGH_ACCURACY) completado en ${elapsed}ms")
            Log.d(TAG, "   Coordenadas: lat=$cachedLatitude, lon=$cachedLongitude")
            Log.d(TAG, "   Precisión: ±${locationResult.accuracy.toInt()}m")
            Log.d(TAG, "   Fuente: GPS + WiFi + Cell Towers (REAL ACTUAL)")
        } else {
            Log.w(TAG, "⚠️ getCurrentLocation devolvió null")
        }
    }

    /**
     * Actualiza la ubicación en background sin bloquear el login
     * Se llama después de usar lastLocation para tener datos REALES ACTUALES
     * para el próximo login
     */
    @SuppressLint("MissingPermission")
    private fun actualizarUbicacionEnBackground() {
        backgroundScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                Log.d(TAG, "🔄 [Background] Actualizando ubicación REAL...")

                obtenerUbicacionActual()

                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "✅ [Background] Ubicación REAL actualizada en ${elapsed}ms")
                Log.d(TAG, "   Esta ubicación se usará en el próximo login (será lastLocation)")
            } catch (e: Exception) {
                Log.e(TAG, "⚠️ [Background] Error actualizando: ${e.message}")
            }
        }
    }
}
