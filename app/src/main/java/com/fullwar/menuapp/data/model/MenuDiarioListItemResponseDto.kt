package com.fullwar.menuapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDiarioListItemResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("fecha")
    val fecha: String,
    @SerialName("descripcionFecha")
    val descripcionFecha: String = "",
    @SerialName("estadoId")
    val estadoId: Int,
    @SerialName("cantidadEntradas")
    val cantidadEntradas: Int,
    @SerialName("cantidadPlatos")
    val cantidadPlatos: List<TipoPlatoCountDto>,
    @SerialName("tiempoTranscurrido")
    val tiempoTranscurrido: String,
    @SerialName("imagenUrl")
    val imagenUrl: String? = null,
    @SerialName("coincidencias")
    val coincidencias: List<CoincidenciaDto>? = null
)
