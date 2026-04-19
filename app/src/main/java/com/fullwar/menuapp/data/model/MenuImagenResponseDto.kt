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
    @SerialName("fechaModificacion") val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion") val usuarioModificacion: String? = null
)
