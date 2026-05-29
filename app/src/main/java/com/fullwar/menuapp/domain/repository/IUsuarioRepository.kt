package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.CambiarContrasenaRequestDto
import com.fullwar.menuapp.data.model.UsuarioResponseDto

interface IUsuarioRepository {
    suspend fun getUsuario(): UsuarioResponseDto
    suspend fun cambiarContrasena(request: CambiarContrasenaRequestDto)
    fun clearCache()
}
