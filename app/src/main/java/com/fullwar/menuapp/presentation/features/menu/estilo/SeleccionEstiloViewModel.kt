package com.fullwar.menuapp.presentation.features.menu.estilo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class SeleccionEstiloViewModel(
    private val menuImagenRepository: IMenuImagenRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionEstiloViewModel"
    }

    var imagenesState by mutableStateOf<State<List<MenuImagenResponseDto>>>(State.Initial)
        private set

    var selectedImagenId by mutableStateOf<Int?>(null)
        private set

    fun selectImagen(id: Int) {
        selectedImagenId = id
    }

    fun loadImagenes() {
        viewModelScope.launch {
            imagenesState = State.Loading
            try {
                val imagenes = menuImagenRepository.getMenuImagenes()
                imagenesState = State.Success(imagenes)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading imagenes", e)
                imagenesState = State.Error(e.message ?: "Error cargando imágenes de fondo")
            }
        }
    }
}
