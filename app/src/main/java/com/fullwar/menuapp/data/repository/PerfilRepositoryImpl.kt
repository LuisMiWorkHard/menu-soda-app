package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.PerfilService
import com.fullwar.menuapp.data.model.PerfilResponseDto
import com.fullwar.menuapp.domain.repository.IPerfilRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerfilRepositoryImpl(
    private val perfilService: PerfilService
) : IPerfilRepository {

    private var cachedPerfil: PerfilResponseDto? = null

    override suspend fun getPerfil(): PerfilResponseDto =
        withContext(Dispatchers.IO) {
            cachedPerfil ?: perfilService.getPerfil().also { cachedPerfil = it }
        }

    override fun clearCache() {
        cachedPerfil = null
    }
}
