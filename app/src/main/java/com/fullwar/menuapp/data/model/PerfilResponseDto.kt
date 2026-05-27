package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerfilResponseDto(
    @SerialName("nombreCompleto") val nombreCompleto: String,
    @SerialName("email") val email: String,
    @SerialName("telefono") val telefono: String = ""
)
