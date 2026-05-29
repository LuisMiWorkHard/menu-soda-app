package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CambiarContrasenaRequestDto(
    @SerialName("contrasenaActual") val contrasenaActual: String,
    @SerialName("contrasenaNueva")  val contrasenaNueva:  String
)
