package com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.domain.repository.IRecuperarContrasenaRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class NuevaContrasenaViewModel(
    private val repo: IRecuperarContrasenaRepository
) : ViewModel() {

    var nuevaContrasena by mutableStateOf("")
        private set

    var confirmarContrasena by mutableStateOf("")
        private set

    var nuevaError by mutableStateOf<Int?>(null)
        private set

    var confirmarError by mutableStateOf<Int?>(null)
        private set

    var serverError by mutableStateOf<String?>(null)
        private set

    var restablecerState by mutableStateOf<State<Unit>>(State.Initial)
        private set

    fun onNuevaChange(value: String) {
        nuevaContrasena = value.take(255)
        nuevaError = null
        serverError = null
    }

    fun onConfirmarChange(value: String) {
        confirmarContrasena = value.take(255)
        confirmarError = null
        serverError = null
    }

    fun guardar() {
        if (!validate()) return
        viewModelScope.launch {
            restablecerState = State.Loading
            try {
                repo.restablecerContrasenaRecuperacion(nuevaContrasena, confirmarContrasena)
                restablecerState = State.Success(Unit)
            } catch (e: ApiException) {
                serverError = e.message ?: "Error al guardar la contraseña"
                restablecerState = State.Error(serverError ?: "")
            } catch (e: Exception) {
                serverError = e.message ?: "Error al guardar la contraseña"
                restablecerState = State.Error(serverError ?: "")
            }
        }
    }

    private fun validate(): Boolean {
        nuevaError = when {
            nuevaContrasena.isBlank()    -> R.string.error_contrasena_nueva_vacia
            nuevaContrasena.length < 8   -> R.string.error_invalid_password
            else                         -> null
        }
        confirmarError = when {
            confirmarContrasena.isBlank()          -> R.string.error_contrasena_nueva_vacia
            confirmarContrasena != nuevaContrasena  -> R.string.error_contrasenas_no_coinciden
            else                                   -> null
        }
        return nuevaError == null && confirmarError == null
    }
}
