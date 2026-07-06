package com.fullwar.menuapp.presentation.features.menu.plato.seleccion

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.domain.repository.IPlatoRepository
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
class SeleccionPlatosFondoViewModel(
    private val platoRepository: IPlatoRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionPlatosFondoViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    var platosState by mutableStateOf<State<List<PlatoResponseDto>>>(State.Initial)
        private set

    var searchResults by mutableStateOf<List<PlatoResponseDto>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    // Lista completa (sin filtro). Fuente reactiva para el estado "sin búsqueda".
    private val allPlatos = MutableStateFlow<List<PlatoResponseDto>>(emptyList())

    init {
        // Único escritor de searchResults: un pipeline reactivo sobre el texto de búsqueda.
        // flatMapLatest cancela la búsqueda anterior en cuanto llega un query nuevo (incluido
        // el vacío), así el último input siempre gana sin importar la latencia de red.
        viewModelScope.launch {
            snapshotFlow { searchQuery.trim() }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        allPlatos
                    } else {
                        flow {
                            delay(SEARCH_DEBOUNCE_MS)
                            emit(
                                runCatching { platoRepository.searchPlatos(query) }
                                    .getOrElse { e ->
                                        Log.e(TAG, "Error searching platos", e)
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

    fun loadPlatos() {
        viewModelScope.launch {
            platosState = State.Loading
            try {
                val platos = platoRepository.getPlatos()
                allPlatos.value = platos
                platosState = State.Success(platos)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading platos", e)
                platosState = State.Error(e.message ?: "Error al cargar platos")
            }
        }
    }

    fun deletePlato(id: Int) {
        viewModelScope.launch {
            try {
                platoRepository.deletePlato(id)
                loadPlatos()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting plato", e)
            }
        }
    }
}
