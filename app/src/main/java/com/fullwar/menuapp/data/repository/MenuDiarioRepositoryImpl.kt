package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.MenuDiarioService
import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MenuDiarioRepositoryImpl(
    private val menuDiarioService: MenuDiarioService
) : IMenuDiarioRepository {
    override suspend fun createMenuDiario(request: MenuDiarioCreateRequestDto, imagenFile: File?): Int =
        withContext(Dispatchers.IO) { menuDiarioService.createMenuDiario(request, imagenFile) }
}
