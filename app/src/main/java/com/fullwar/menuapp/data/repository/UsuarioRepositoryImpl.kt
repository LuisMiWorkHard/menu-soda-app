package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.UsuarioService
import com.fullwar.menuapp.data.model.CambiarContrasenaRequestDto
import com.fullwar.menuapp.data.model.UsuarioResponseDto
import com.fullwar.menuapp.domain.repository.IUsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepositoryImpl(
    private val usuarioService: UsuarioService
) : IUsuarioRepository {

    private var cachedUsuario: UsuarioResponseDto? = null

    override suspend fun getUsuario(): UsuarioResponseDto =
        withContext(Dispatchers.IO) {
            cachedUsuario ?: usuarioService.getUsuario().also { cachedUsuario = it }
        }

    override suspend fun cambiarContrasena(request: CambiarContrasenaRequestDto) =
        withContext(Dispatchers.IO) { usuarioService.cambiarContrasena(request) }

    override fun clearCache() {
        cachedUsuario = null
    }
}
