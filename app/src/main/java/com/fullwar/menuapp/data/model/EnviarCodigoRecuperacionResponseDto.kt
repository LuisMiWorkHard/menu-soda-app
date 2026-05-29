package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnviarCodigoRecuperacionResponseDto(
    @SerialName("emailMasked") val emailMasked: String
)
