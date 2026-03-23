package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioDetailResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("fecha")
    val fecha: String,
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("fechaRegistro")
    val fechaRegistro: String,
    @SerialName("usuarioRegistro")
    val usuarioRegistro: String,
    @SerialName("fechaModificacion")
    val fechaModificacion: String? = null,
    @SerialName("usuarioModificacion")
    val usuarioModificacion: String? = null,
    @SerialName("entradas")
    val entradas: List<MenuDiarioEntradaResponseDto>,
    @SerialName("platos")
    val platos: List<MenuDiarioPlatoWithAdicionalResponseDto>,
    @SerialName("imagen")
    val imagen: MenuDiarioImagenResponseDto? = null
)