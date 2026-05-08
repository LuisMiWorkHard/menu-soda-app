package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.MenuDiarioService
import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import com.fullwar.menuapp.data.model.MenuDiarioDetailResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioUpdateRequestDto
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MenuDiarioRepositoryImpl(
    private val menuDiarioService: MenuDiarioService
) : IMenuDiarioRepository {
    override suspend fun createMenuDiario(request: MenuDiarioCreateRequestDto, imagenFile: File?): Int =
        withContext(Dispatchers.IO) { menuDiarioService.createMenuDiario(request, imagenFile) }

    override suspend fun getMenusDiarios(busqueda: String?): List<MenuDiarioListItemResponseDto> =
        withContext(Dispatchers.IO) { menuDiarioService.getMenusDiarios(busqueda) }

    override suspend fun getMenuDiarioById(id: Int): MenuDiarioDetailResponseDto =
        withContext(Dispatchers.IO) { menuDiarioService.getMenuDiarioById(id) }

    override suspend fun updateMenuDiario(id: Int, request: MenuDiarioUpdateRequestDto, imagenFile: File?): Boolean =
        withContext(Dispatchers.IO) { menuDiarioService.updateMenuDiario(id, request, imagenFile) }

    override suspend fun deleteMenuDiario(id: Int) =
        withContext(Dispatchers.IO) { menuDiarioService.deleteMenuDiario(id) }
}
