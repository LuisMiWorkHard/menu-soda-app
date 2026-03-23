package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipoPlatoCountDto(
    @SerialName("tipoPlato")
    val tipoPlato: String,
    @SerialName("cantidad")
    val cantidad: Int
)