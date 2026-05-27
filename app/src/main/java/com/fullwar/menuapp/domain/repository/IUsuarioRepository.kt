package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.UsuarioResponseDto

interface IUsuarioRepository {
    suspend fun getUsuario(): UsuarioResponseDto
    fun clearCache()
}
