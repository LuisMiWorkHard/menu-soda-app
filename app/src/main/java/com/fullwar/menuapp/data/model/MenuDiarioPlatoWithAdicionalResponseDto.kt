package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioPlatoWithAdicionalResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("platoId")
    val platoId: Int,
    @SerialName("platoNombre")
    val platoNombre: String,
    @SerialName("platoDescripcion")
    val platoDescripcion: String? = null,
    @SerialName("tipoPlatoId")
    val tipoPlatoId: Int,
    @SerialName("tipoPlatoDescripcion")
    val tipoPlatoDescripcion: String,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("adicional")
    val adicional: MenuDiarioPlatoAdicionalResponseDto? = null
)