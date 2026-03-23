package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.EntradaService
import com.fullwar.menuapp.data.datasource.remote.ImagenService
import com.fullwar.menuapp.data.datasource.remote.TipoEntradaService
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntradaRepositoryImpl(
    private val entradaService: EntradaService,
    private val tipoEntradaService: TipoEntradaService,
    private val imagenService: ImagenService
) {

    suspend fun getEntradas(): List<EntradaResponseDto> {
        return withContext(Dispatchers.IO) {
            entradaService.getEntradas()
        }
    }

    suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaResponseDto {
        return withContext(Dispatchers.IO) {
            entradaService.createEntrada(request)
        }
    }

    suspend fun getTiposEntrada(): List<TipoEntradaResponseDto> {
        return withContext(Dispatchers.IO) {
            tipoEntradaService.getTiposEntrada()
        }
    }

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        extension: String
    ): ImagenResponseDto {
        return withContext(Dispatchers.IO) {
            imagenService.uploadImage(imageBytes, fileName, extension)
        }
    }
}
