package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuImagenResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("imagenId") val imagenId: Int,
    @SerialName("imagenUrl") val imagenUrl: String,
    @SerialName("estadoId") val estadoId: Int,
    @SerialName("fechaRegistro") val fechaRegistro: String,
    @SerialName("usuarioRegistro") val usuarioRegistro: String,
    @SerialName("aretextop") val areaTextoTop: Float = 0.10f,
    @SerialName("aretexbot") val areaTextoBottom: Float = 0.10f,
    @SerialName("aretexini") val areaTextoInicio: Float = 0.08f,
    @SerialName("aretexfin") val areaTextoFin: Float = 0.08f,
    @SerialName("maxfonsiz") val maxFontSize: Float = 17.0f,
    @SerialName("fonfam")    val fontFamily: String = "default",
    @SerialName("fechaModificacion") val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion") val usuarioModificacion: String? = null
)
