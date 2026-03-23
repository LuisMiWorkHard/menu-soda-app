package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioEntradaResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("entradaId")
    val entradaId: Int,
    @SerialName("entradaNombre")
    val entradaNombre: String,
    @SerialName("entradaDescripcion")
    val entradaDescripcion: String? = null,
    @SerialName("tipoEntradaId")
    val tipoEntradaId: Int,
    @SerialName("tipoEntradaDescripcion")
    val tipoEntradaDescripcion: String,
    @SerialName("estadoId")
    val estadoId: Int
)