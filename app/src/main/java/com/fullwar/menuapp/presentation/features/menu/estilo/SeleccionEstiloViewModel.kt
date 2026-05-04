package com.fullwar.menuapp.presentation.features.menu.estilo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import com.fullwar.menuapp.data.model.MenuDiarioPlatoRequestDto
import com.fullwar.menuapp.data.model.MenuImagenResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import com.fullwar.menuapp.domain.repository.IMenuImagenRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class SeleccionEstiloViewModel(
    private val menuImagenRepository: IMenuImagenRepository,
    private val menuDiarioRepository: IMenuDiarioRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SeleccionEstiloViewModel"
    }

    var imagenesState by mutableStateOf<State<List<MenuImagenResponseDto>>>(State.Initial)
        private set

    var selectedImagenId by mutableStateOf<Int?>(null)
        private set

    var saveState by mutableStateOf<SaveUiState>(SaveUiState.Idle)
        private set

    var triggerCapture by mutableStateOf(false)
        private set

    fun selectImagen(id: Int) {
        selectedImagenId = id
    }

    fun clearSelectedImagen() {
        selectedImagenId = null
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

    fun onFinalizarClicked() {
        if (selectedImagenId == null) return
        triggerCapture = true
    }

    fun onCaptureHandled() {
        triggerCapture = false
    }

    fun guardarMenuDiario(
        entradas: List<EntradaResponseDto>,
        platos: List<PlatoResponseDto>,
        imagenFile: File?
    ) {
        viewModelScope.launch {
            saveState = SaveUiState.Loading
            try {
                val request = MenuDiarioCreateRequestDto(
                    fecha = LocalDate.now().toString(),
                    entradasIds = entradas.map { it.id },
                    platos = platos.map { MenuDiarioPlatoRequestDto(platoId = it.id) }
                )
                val id = menuDiarioRepository.createMenuDiario(request, imagenFile)
                saveState = SaveUiState.Success(id, imagenFile)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving menu diario", e)
                saveState = SaveUiState.Error(e.message ?: "Error guardando el menú")
            }
        }
    }

    fun resetSaveState() {
        saveState = SaveUiState.Idle
    }
}

sealed class SaveUiState {
    object Idle : SaveUiState()
    object Loading : SaveUiState()
    data class Success(val menuId: Int, val imagenFile: File?) : SaveUiState()
    data class Error(val message: String) : SaveUiState()
}
