package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import java.io.File

interface IMenuDiarioRepository {
    suspend fun createMenuDiario(request: MenuDiarioCreateRequestDto, imagenFile: File?): Int
}
