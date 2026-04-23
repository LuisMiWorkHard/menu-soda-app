package com.fullwar.menuapp.presentation.features.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto

class MenuViewModel : ViewModel() {
    var selectedEntradas by mutableStateOf(listOf<EntradaResponseDto>())
        private set

    var selectedPlatosFuertes by mutableStateOf(listOf<PlatoResponseDto>())
        private set

    var selectedBebidas by mutableStateOf(listOf<String>())
        private set

    var showSugerencias by mutableStateOf(true)
        private set

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
