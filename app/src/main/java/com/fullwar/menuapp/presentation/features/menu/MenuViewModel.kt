package com.fullwar.menuapp.presentation.features.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto

class MenuViewModel : ViewModel() {
    var selectedEntradas by mutableStateOf(setOf<EntradaResponseDto>())
        private set

    var selectedPlatosFuertes by mutableStateOf(setOf<PlatoResponseDto>())
        private set

    var selectedBebidas by mutableStateOf(setOf<String>())
        private set

    var showSugerencias by mutableStateOf(true)
        private set

    fun updateEntradas(entradas: Set<EntradaResponseDto>) {
        selectedEntradas = entradas
    }

    fun updatePlatosFuertes(platos: Set<PlatoResponseDto>) {
        selectedPlatosFuertes = platos
    }

    fun hideSugerencias() {
        showSugerencias = false
    }
}
