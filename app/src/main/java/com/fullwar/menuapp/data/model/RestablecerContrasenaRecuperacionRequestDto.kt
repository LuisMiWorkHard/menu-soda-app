package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestablecerContrasenaRecuperacionRequestDto(
    @SerialName("nuevaContrasena")     val nuevaContrasena: String,
    @SerialName("confirmarContrasena") val confirmarContrasena: String
)
