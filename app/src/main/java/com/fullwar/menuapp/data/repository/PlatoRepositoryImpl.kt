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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlatoRepositoryImpl(
    private val platoService: PlatoService,
    private val tipoPlatoService: TipoPlatoService,
    private val imagenService: ImagenService
) : IPlatoRepository {

    override suspend fun getPlatos(): List<PlatoResponseDto> =
        withContext(Dispatchers.IO) { platoService.getPlatos() }

    override suspend fun createPlato(request: PlatoCreateRequestDto): PlatoCreateResponseDto =
        withContext(Dispatchers.IO) { platoService.createPlato(request) }

    override suspend fun updatePlato(id: Int, request: PlatoUpdateRequestDto) =
        withContext(Dispatchers.IO) { platoService.updatePlato(id, request) }

    override suspend fun getTiposPlato(): List<TipoPlatoResponseDto> =
        withContext(Dispatchers.IO) { tipoPlatoService.getTiposPlato() }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        extension: String
    ): ImagenResponseDto =
        withContext(Dispatchers.IO) { imagenService.uploadImage(imageBytes, fileName, extension) }
}
