package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntradaCreateRequestDto(
    @SerialName("nombre")
    val nombre: String,
    @SerialName("descripcion")
    val descripcion: String = "",
    @SerialName("tipoEntradaId")
    val tipoEntradaId: Int,
    @SerialName("imagenId")
    val imagenId: Int? = null
)
