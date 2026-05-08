package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import com.fullwar.menuapp.data.model.MenuDiarioDetailResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioUpdateRequestDto
import java.io.File

interface IMenuDiarioRepository {
    suspend fun createMenuDiario(request: MenuDiarioCreateRequestDto, imagenFile: File?): Int
    suspend fun getMenusDiarios(busqueda: String? = null): List<MenuDiarioListItemResponseDto>
    suspend fun getMenuDiarioById(id: Int): MenuDiarioDetailResponseDto
    suspend fun updateMenuDiario(id: Int, request: MenuDiarioUpdateRequestDto, imagenFile: File?): Boolean
    suspend fun deleteMenuDiario(id: Int)
}
