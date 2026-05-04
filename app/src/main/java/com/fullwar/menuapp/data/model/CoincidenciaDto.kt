package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoincidenciaDto(
    @SerialName("tipo") val tipo: String,
    @SerialName("nombre") val nombre: String
)
