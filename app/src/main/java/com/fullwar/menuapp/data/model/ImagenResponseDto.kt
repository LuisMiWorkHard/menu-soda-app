package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImagenResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("ruta")
    val ruta: String,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("extension")
    val extension: String,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("fechaRegistro")
    val fechaRegistro: String,
    @SerialName("usuarioRegistro")
    val usuarioRegistro: String,
    @SerialName("fechaModificacion")
    val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion")
    val usuarioModificacion: String? = null
)