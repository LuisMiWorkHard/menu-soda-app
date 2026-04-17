package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.EntradaService
import com.fullwar.menuapp.data.datasource.remote.ImagenService
import com.fullwar.menuapp.data.datasource.remote.TipoEntradaService
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.TipoEntradaResponseDto
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.presentation.common.utils.FuzzyMatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntradaRepositoryImpl(
    private val entradaService: EntradaService,
    private val tipoEntradaService: TipoEntradaService,
    private val imagenService: ImagenService
) : IEntradaRepository {

    private var cachedEntradas: List<EntradaResponseDto>? = null

    override suspend fun getEntradas(): List<EntradaResponseDto> =
        withContext(Dispatchers.IO) {
            entradaService.getEntradas().also { cachedEntradas = it }
        }

    override suspend fun searchEntradas(query: String): List<EntradaResponseDto> =
        withContext(Dispatchers.IO) {
            entradaService.getEntradas(filter = query)
        }

    override suspend fun findSimilarEntradas(nombre: String, excludeId: Int?): List<EntradaResponseDto> =
        withContext(Dispatchers.IO) {
            val list = cachedEntradas ?: entradaService.getEntradas().also { cachedEntradas = it }
            list.filter { it.id != excludeId && FuzzyMatcher.isDuplicate(nombre, it.nombre) }.take(3)
        }

    override suspend fun createEntrada(request: EntradaCreateRequestDto): EntradaCreateResponseDto =
        withContext(Dispatchers.IO) {
            entradaService.createEntrada(request).also { cachedEntradas = null }
        }

    override suspend fun updateEntrada(id: Int, request: EntradaUpdateRequestDto): EntradaResponseDto =
        withContext(Dispatchers.IO) {
            entradaService.updateEntrada(id, request).also { cachedEntradas = null }
        }

    override suspend fun getTiposEntrada(): List<TipoEntradaResponseDto> =
        withContext(Dispatchers.IO) {
            tipoEntradaService.getTiposEntrada()
        }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        extension: String
    ): ImagenResponseDto =
        withContext(Dispatchers.IO) {
            imagenService.uploadImage(imageBytes, fileName, extension)
        }
}
