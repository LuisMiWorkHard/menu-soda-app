package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntradaResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("descripcionLarga")
    val descripcionLarga: String,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("tipoEntradaId")
    val tipoEntradaId: Int,
    @SerialName("imagenId")
    val imagenId: Int,
    @SerialName("fechaRegistro")
    val fechaRegistro: String,
    @SerialName("usuarioRegistro")
    val usuarioRegistro: String,
    @SerialName("fechaModificacion")
    val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion")
    val usuarioModificacion: String? = null
)