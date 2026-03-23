package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatoUpdateRequestDto(
    @SerialName("id")
    val id: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("tipoPlatoId")
    val tipoPlatoId: Int,
    @SerialName("estadoId")
    val estadoId: Int
)