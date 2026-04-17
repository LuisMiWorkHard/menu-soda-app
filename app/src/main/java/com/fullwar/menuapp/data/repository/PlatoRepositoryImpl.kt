package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.ImagenService
import com.fullwar.menuapp.data.datasource.remote.PlatoService
import com.fullwar.menuapp.data.datasource.remote.TipoPlatoService
import com.fullwar.menuapp.data.model.ImagenResponseDto
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.PlatoUpdateRequestDto
import com.fullwar.menuapp.data.model.TipoPlatoResponseDto
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.utils.FuzzyMatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlatoRepositoryImpl(
    private val platoService: PlatoService,
    private val tipoPlatoService: TipoPlatoService,
    private val imagenService: ImagenService
) : IPlatoRepository {

    private var cachedPlatos: List<PlatoResponseDto>? = null

    override suspend fun getPlatos(): List<PlatoResponseDto> =
        withContext(Dispatchers.IO) {
            platoService.getPlatos().also { cachedPlatos = it }
        }

    override suspend fun searchPlatos(query: String): List<PlatoResponseDto> =
        withContext(Dispatchers.IO) {
            platoService.getPlatos(nombre = query)
        }

    override suspend fun findSimilarPlatos(nombre: String, excludeId: Int?): List<PlatoResponseDto> =
        withContext(Dispatchers.IO) {
            val list = cachedPlatos ?: platoService.getPlatos().also { cachedPlatos = it }
            list.filter { it.id != excludeId && FuzzyMatcher.isDuplicate(nombre, it.nombre) }.take(3)
        }

    override suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto =
        withContext(Dispatchers.IO) {
            platoService.createPlato(request).also { cachedPlatos = null }
        }

    override suspend fun updatePlato(id: Int, request: PlatoUpdateRequestDto) =
        withContext(Dispatchers.IO) {
            platoService.updatePlato(id, request).also { cachedPlatos = null }
        }

    override suspend fun getTiposPlato(): List<TipoPlatoResponseDto> =
        withContext(Dispatchers.IO) { tipoPlatoService.getTiposPlato() }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        extension: String
    ): ImagenResponseDto =
        withContext(Dispatchers.IO) { imagenService.uploadImage(imageBytes, fileName, extension) }
}
