package com.fullwar.menuapp.presentation.features.menu.entrada.seleccion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SeleccionEntradasViewModel(
    private val entradaRepository: IEntradaRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionEntradasViewModel"
    }

    var entradasState by mutableStateOf<State<List<EntradaResponseDto>>>(State.Initial)
        private set

    var searchResults by mutableStateOf<List<EntradaResponseDto>>(emptyList())
        private set

    private var searchJob: Job? = null

    fun loadEntradas() {
        viewModelScope.launch {
            entradasState = State.Loading
            try {
                val entradas = entradaRepository.getEntradas()
                entradasState = State.Success(entradas)
                searchResults = entradas
            } catch (e: Exception) {
                Log.e(TAG, "Error loading entradas", e)
                entradasState = State.Error(e.message ?: "Error cargando entradas")
            }
        }
    }

    fun searchEntradas(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                searchResults = entradaRepository.searchEntradas(query)
            } catch (e: Exception) {
                Log.e(TAG, "Error searching entradas", e)
            }
        }
    }

    fun resetSearch() {
        val state = entradasState
        if (state is State.Success) searchResults = state.data
    }

    fun deleteEntrada(id: Int) {
        viewModelScope.launch {
            try {
                entradaRepository.deleteEntrada(id)
                loadEntradas()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting entrada", e)
            }
        }
    }
}
