package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipoPlatoUpdateRequestDto(
    @SerialName("id")
    val id: Int,
    @SerialName("descripcion")
    val descripcion: String,
    @SerialName("estadoId")
    val estadoId: Int
)