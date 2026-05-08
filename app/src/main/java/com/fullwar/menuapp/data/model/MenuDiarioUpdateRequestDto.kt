package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioUpdateRequestDto(
    @SerialName("id")
    val id: Int,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("entradasIds")
    val entradasIds: List<Int>,
    @SerialName("platos")
    val platos: List<MenuDiarioPlatoRequestDto>,
    @SerialName("imagenId")
    val imagenId: Int? = null,
    @SerialName("menuImagenId")
    val menuImagenId: Int? = null
)