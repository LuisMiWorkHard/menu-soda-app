package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiValidationErrorResponseDto(
    @SerialName("detail")
    val detail: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("errors")
    val errors: Map<String, List<String>>? = null
)
