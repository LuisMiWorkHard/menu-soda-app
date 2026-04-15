package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatoCreateResponseDto(
    @SerialName("id")
    val id: Int
)
