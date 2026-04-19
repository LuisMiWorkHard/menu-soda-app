package com.fullwar.menuapp.presentation.features.menu.plato.seleccion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SeleccionPlatosFondoViewModel(
    private val platoRepository: IPlatoRepository,
    private val menuImagenRepository: IMenuImagenRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionPlatosFondoViewModel"
    }

    var platosState by mutableStateOf<State<List<PlatoResponseDto>>>(State.Initial)
        private set

    var searchResults by mutableStateOf<List<PlatoResponseDto>>(emptyList())
        private set

    var imagenesMap by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    private var searchJob: Job? = null

    fun loadPlatos() {
        viewModelScope.launch {
            platosState = State.Loading
            try {
                val platos = platoRepository.getPlatos()
                val imagenes = menuImagenRepository.getMenuImagenes()
                imagenesMap = imagenes.associate { it.imagenId to it.imagenUrl }
                platosState = State.Success(platos)
                searchResults = platos
            } catch (e: Exception) {
                Log.e(TAG, "Error loading platos", e)
                platosState = State.Error(e.message ?: "Error al cargar platos")
            }
        }
    }

    fun searchPlatos(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                searchResults = platoRepository.searchPlatos(query)
            } catch (e: Exception) {
                Log.e(TAG, "Error searching platos", e)
            }
        }
    }

    fun resetSearch() {
        val state = platosState
        if (state is State.Success) searchResults = state.data
    }
}
