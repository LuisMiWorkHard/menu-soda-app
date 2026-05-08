package com.fullwar.menuapp.presentation.features.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import kotlinx.coroutines.launch

class MenuViewModel(private val repo: IMenuDiarioRepository) : ViewModel() {

    var menuId by mutableStateOf<Int?>(null)
        private set

    val isEditMode get() = menuId != null

    var selectedEntradas by mutableStateOf(listOf<EntradaResponseDto>())
        private set

    var selectedPlatosFuertes by mutableStateOf(listOf<PlatoResponseDto>())
        private set

    var selectedBebidas by mutableStateOf(listOf<String>())
        private set

    var showSugerencias by mutableStateOf(true)
        private set

    var preSelectedImagenId by mutableStateOf<Int?>(null)
        private set

    // IDs de entradas y platos del menú a editar; se usan para seleccionarlos
    // desde los listados completos de la API una vez que cargan.
    var preSelectedEntradasIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    var preSelectedPlatosIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    var isLoadingMenuDetail by mutableStateOf(false)
        private set

    var menuDetailError by mutableStateOf<String?>(null)
        private set

    fun initEditMode(id: Int) {
        if (menuId == id) return
        menuId = id
        selectedEntradas = emptyList()
        selectedPlatosFuertes = emptyList()
        preSelectedEntradasIds = emptySet()
        preSelectedPlatosIds = emptySet()
        preSelectedImagenId = null
        showSugerencias = false
        loadMenuDetail(id)
    }

    fun retryLoadMenuDetail() {
        menuDetailError = null
        menuId?.let { loadMenuDetail(it) }
    }

    private fun loadMenuDetail(id: Int) {
        viewModelScope.launch {
            isLoadingMenuDetail = true
            menuDetailError = null
            runCatching { repo.getMenuDiarioById(id) }
                .onSuccess { detail ->
                    preSelectedImagenId = detail.imagen?.menuImagenId
                    preSelectedEntradasIds = detail.entradas.map { it.entradaId }.toSet()
                    preSelectedPlatosIds = detail.platos.map { it.platoId }.toSet()
                    isLoadingMenuDetail = false
                }
                .onFailure { e ->
                    menuDetailError = e.message ?: "Error al cargar el menú"
                    isLoadingMenuDetail = false
                }
        }
    }

    fun updateEntradas(entradas: List<EntradaResponseDto>) {
        selectedEntradas = entradas
    }

    fun updatePlatosFuertes(platos: List<PlatoResponseDto>) {
        selectedPlatosFuertes = platos
    }

    fun moveEntrada(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in selectedEntradas.indices || toIndex !in selectedEntradas.indices) return
        val newList = selectedEntradas.toMutableList()
        val item = newList.removeAt(fromIndex)
        newList.add(toIndex, item)
        selectedEntradas = newList
    }

    fun movePlato(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in selectedPlatosFuertes.indices || toIndex !in selectedPlatosFuertes.indices) return
        val newList = selectedPlatosFuertes.toMutableList()
        val item = newList.removeAt(fromIndex)
        newList.add(toIndex, item)
        selectedPlatosFuertes = newList
    }

    fun hideSugerencias() {
        showSugerencias = false
    }
}
