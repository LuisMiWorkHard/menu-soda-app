package com.fullwar.menuapp.presentation.features.home.tabs.perfil.informacion_personal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.UsuarioResponseDto
import com.fullwar.menuapp.domain.repository.IUsuarioRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class InformacionPersonalViewModel(
    private val repo: IUsuarioRepository
) : ViewModel() {

    var usuarioState by mutableStateOf<State<UsuarioResponseDto>>(State.Initial)
        private set

    fun loadUsuario() {
        if (usuarioState is State.Success) return
        viewModelScope.launch {
            usuarioState = State.Loading
            try {
                usuarioState = State.Success(repo.getUsuario())
            } catch (e: Exception) {
                usuarioState = State.Error(e.message ?: "Error cargando información del usuario")
            }
        }
    }
}
