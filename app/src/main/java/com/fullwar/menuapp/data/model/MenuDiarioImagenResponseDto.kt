package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioImagenResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("imagenId")
    val imagenId: Int,
    @SerialName("menuImagenId")
    val menuImagenId: Int? = null,
    @SerialName("ruta")
    val ruta: String,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("extension")
    val extension: String,
    @SerialName("estadoId")
    val estadoId: Int
)