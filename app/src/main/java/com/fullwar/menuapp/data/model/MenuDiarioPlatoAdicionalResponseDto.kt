package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioPlatoAdicionalResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("menuDiarioPlatoId")
    val menuDiarioPlatoId: Int,
    @SerialName("adicionalId")
    val adicionalId: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("descripcion")
    val descripcion: String? = null,
    @SerialName("estadoId")
    val estadoId: Int
)