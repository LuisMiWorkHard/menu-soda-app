package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto

interface IEntradaRepository {
    suspend fun getEntradas(): List<EntradaResponseDto>
    suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto
    suspend fun updateEntrada(id: Int, request: EntradaUpdateRequestDto): EntradaResponseDto
    suspend fun getTiposEntrada(): List<TipoEntradaResponseDto>
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String, extension: String): ImagenResponseDto
}
