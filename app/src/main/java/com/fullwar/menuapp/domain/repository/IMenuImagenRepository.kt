package com.fullwar.menuapp.domain.repository

import com.fullwar.menuapp.data.model.MenuImagenResponseDto

interface IMenuImagenRepository {
    suspend fun getMenuImagenes(): List<MenuImagenResponseDto>
}
