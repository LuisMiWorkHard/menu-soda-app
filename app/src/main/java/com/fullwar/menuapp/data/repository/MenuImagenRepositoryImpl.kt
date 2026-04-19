package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.MenuImagenService
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MenuImagenRepositoryImpl(
    private val menuImagenService: MenuImagenService
) : IMenuImagenRepository {
    override suspend fun getMenuImagenes(): List<MenuImagenResponseDto> =
        withContext(Dispatchers.IO) { menuImagenService.getMenuImagenes() }
}
