package com.fullwar.menuapp.presentation.features.menu.entrada.seleccion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SeleccionEntradasViewModel(
    private val entradaRepository: IEntradaRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionEntradasViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    var entradasState by mutableStateOf<State<List<EntradaResponseDto>>>(State.Initial)
        private set

    var searchResults by mutableStateOf<List<EntradaResponseDto>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    // Lista completa (sin filtro). Fuente reactiva para el estado "sin búsqueda".
    private val allEntradas = MutableStateFlow<List<EntradaResponseDto>>(emptyList())

    init {
        // Único escritor de searchResults: un pipeline reactivo sobre el texto de búsqueda.
        // flatMapLatest cancela la búsqueda anterior en cuanto llega un query nuevo (incluido
        // el vacío), así el último input siempre gana sin importar la latencia de red.
        viewModelScope.launch {
            snapshotFlow { searchQuery.trim() }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        allEntradas
                    } else {
                        flow {
                            delay(SEARCH_DEBOUNCE_MS)
                            emit(
                                runCatching { entradaRepository.searchEntradas(query) }
                                    .getOrElse { e ->
                                        Log.e(TAG, "Error searching entradas", e)
                                        emptyList()
                                    }
                            )
                        }
                    }
                }
                .collect { searchResults = it }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun loadEntradas() {
        viewModelScope.launch {
            entradasState = State.Loading
            try {
                val entradas = entradaRepository.getEntradas()
                allEntradas.value = entradas
                entradasState = State.Success(entradas)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading entradas", e)
                entradasState = State.Error(e.message ?: "Error cargando entradas")
            }
        }
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
