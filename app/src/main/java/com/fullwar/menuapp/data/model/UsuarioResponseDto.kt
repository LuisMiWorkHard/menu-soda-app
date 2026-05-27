package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioResponseDto(
    @SerialName("nombreCompleto") val nombreCompleto: String,
    @SerialName("documento") val documento: String = "",
    @SerialName("email") val email: String,
    @SerialName("telefono") val telefono: String = "",
    @SerialName("genero") val genero: String = "",
    @SerialName("fechaNacimiento") val fechaNacimiento: String = "",
    @SerialName("direccionCasa") val direccionCasa: String? = null
)
