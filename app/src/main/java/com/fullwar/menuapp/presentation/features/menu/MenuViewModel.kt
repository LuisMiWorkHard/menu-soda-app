package com.fullwar.menuapp.presentation.features.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MenuViewModel : ViewModel() {
    var selectedEntradas by mutableStateOf(setOf<String>())
        private set

    var selectedPlatosFuertes by mutableStateOf(setOf<String>())
        private set

    var selectedBebidas by mutableStateOf(setOf<String>())
        private set

    var showSugerencias by mutableStateOf(true)
        private set

    fun updateEntradas(entradas: Set<String>) {
        selectedEntradas = entradas
    }

    fun updatePlatosFuertes(platos: Set<String>) {
        selectedPlatosFuertes = platos
    }

    fun hideSugerencias() {
        showSugerencias = false
    }
}
