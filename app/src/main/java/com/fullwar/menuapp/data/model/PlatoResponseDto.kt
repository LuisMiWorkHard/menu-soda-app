package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatoResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("tipoPlatoId")
    val tipoPlatoId: Int,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("fechaRegistro")
    val fechaRegistro: String,
    @SerialName("usuarioRegistro")
    val usuarioRegistro: String,
    @SerialName("fechaModificacion")
    val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion")
    val usuarioModificacion: String? = null
)