package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("tipoDocumento")
    val tipoDocumento: Int,
    @SerialName("numeroDocumento")
    val numeroDocumento: String,
    @SerialName("contrasena")
    val contrasena: String
)
