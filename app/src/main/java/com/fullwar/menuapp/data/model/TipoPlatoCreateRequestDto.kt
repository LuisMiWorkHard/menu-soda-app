package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipoPlatoCreateRequestDto(
    @SerialName("descripcion")
    val descripcion: String
)