package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto

interface IPlatoRepository {
    suspend fun getPlatos(): List<PlatoResponseDto>
    suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto
    suspend fun getTiposPlato(): List<TipoPlatoResponseDto>
}
