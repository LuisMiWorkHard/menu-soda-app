package com.fullwar.menuapp.data.datasource.local

interface LocationProvider {
    fun getLatitude(): String?
    fun getLongitude(): String?
    suspend fun actualizarUbicacion(permisosConcedidos: Boolean)
}
