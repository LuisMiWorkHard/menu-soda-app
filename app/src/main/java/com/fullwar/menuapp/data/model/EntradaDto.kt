package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntradaCreateRequestDto(
    @SerialName("entdes")
    val entdes: String,
    @SerialName("entdeslar")
    val entdeslar: String? = null,
    @SerialName("codtipent")
    val codtipent: Int,
    @SerialName("codima")
    val codima: Int? = null
)

@Serializable
data class EntradaResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("entdes")
    val entdes: String,
    @SerialName("entdeslar")
    val entdeslar: String? = null,
    @SerialName("codest")
    val codest: Int,
    @SerialName("codtipent")
    val codtipent: Int,
    @SerialName("codima")
    val codima: Int? = null,
    @SerialName("fecreg")
    val fecreg: String,
    @SerialName("usureg")
    val usureg: String,
    @SerialName("fecmod")
    val fecmod: String? = null,
    @SerialName("usumod")
    val usumod: String? = null
)

@Serializable
data class TipoEntradaResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("tipentdes")
    val tipentdes: String,
    @SerialName("codest")
    val codest: Int,
    @SerialName("fecreg")
    val fecreg: String,
    @SerialName("usureg")
    val usureg: String,
    @SerialName("fecmod")
    val fecmod: String? = null,
    @SerialName("usumod")
    val usumod: String? = null
)

@Serializable
data class ImagenUploadResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("imarut")
    val imarut: String
)
