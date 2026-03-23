package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioPlatoRequestDto(
    @SerialName("platoId")
    val platoId: Int,
    @SerialName("adicionalId")
    val adicionalId: Int? = null
)
