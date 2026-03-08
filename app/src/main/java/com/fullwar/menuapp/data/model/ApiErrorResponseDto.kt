package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponseDto(
    @SerialName("title")
    val title: String? = null,
    @SerialName("detail")
    val detail: String? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("code")
    val code: String? = null
)
