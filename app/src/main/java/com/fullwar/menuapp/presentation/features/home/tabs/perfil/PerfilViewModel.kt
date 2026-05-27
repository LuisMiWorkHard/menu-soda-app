package com.fullwar.menuapp.presentation.features.home.tabs.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.UsuarioResponseDto
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.domain.repository.IUsuarioRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repo: IUsuarioRepository,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    var perfilState by mutableStateOf<State<UsuarioResponseDto>>(State.Initial)
        private set

    fun loadPerfil() {
        if (perfilState is State.Success) return
        viewModelScope.launch {
            perfilState = State.Loading
            try {
                perfilState = State.Success(repo.getUsuario())
            } catch (e: Exception) {
                perfilState = State.Error(e.message ?: "Error cargando datos del usuario")
            }
        }
    }

    fun cerrarSesion(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logoutAsync()
            repo.clearCache()
            onLoggedOut()
        }
    }
}
