package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.PerfilResponseDto

interface IPerfilRepository {
    suspend fun getPerfil(): PerfilResponseDto
    fun clearCache()
}
