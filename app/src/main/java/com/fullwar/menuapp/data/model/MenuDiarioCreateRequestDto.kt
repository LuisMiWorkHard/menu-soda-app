package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioCreateRequestDto(
    @SerialName("fecha")
    val fecha: String,
    @SerialName("entradasIds")
    val entradasIds: List<Int>,
    @SerialName("platos")
    val platos: List<MenuDiarioPlatoRequestDto>
)