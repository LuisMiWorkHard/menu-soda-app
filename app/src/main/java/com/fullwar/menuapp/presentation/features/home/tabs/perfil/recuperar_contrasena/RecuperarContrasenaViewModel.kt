package com.fullwar.menuapp.presentation.features.home.tabs.perfil.recuperar_contrasena

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.domain.repository.IAuthRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecuperarContrasenaViewModel(
    private val repo: IAuthRepository
) : ViewModel() {

    var enviarState by mutableStateOf<State<String>>(State.Initial)
        private set

    var verificarState by mutableStateOf<State<Unit>>(State.Initial)
        private set

    val codigo = mutableStateListOf("", "", "", "")

    var tiempoRestante by mutableIntStateOf(0)
        private set

    private var countdownJob: Job? = null

    fun enviarCodigo() {
        viewModelScope.launch {
            enviarState = State.Loading
            try {
                val emailMasked = repo.enviarCodigoRecuperacion()
                enviarState = State.Success(emailMasked)
                verificarState = State.Initial
                reiniciarCodigo()
                iniciarCountdown()
            } catch (e: ApiException) {
                enviarState = State.Error(e.message ?: "Error al enviar el código")
            } catch (e: Exception) {
                enviarState = State.Error(e.message ?: "Error al enviar el código")
            }
        }
    }

    fun onDigitChange(index: Int, value: String) {
        val digit = value.filter { it.isDigit() }.take(1)
        codigo[index] = digit
        if (digit.isNotEmpty() && index < 3) return
        if (codigo.all { it.isNotEmpty() }) verificarCodigo()
    }

    fun verificarCodigo() {
        val codigoCompleto = codigo.joinToString("")
        if (codigoCompleto.length < 4) return
        viewModelScope.launch {
            verificarState = State.Loading
            try {
                repo.verificarCodigoRecuperacion(codigoCompleto)
                verificarState = State.Success(Unit)
            } catch (e: ApiException) {
                verificarState = State.Error(e.message ?: "Código incorrecto")
                reiniciarCodigo()
            } catch (e: Exception) {
                verificarState = State.Error(e.message ?: "Error al verificar el código")
                reiniciarCodigo()
            }
        }
    }

    fun reenviarCodigo() {
        countdownJob?.cancel()
        tiempoRestante = 0
        enviarCodigo()
    }

    private fun reiniciarCodigo() {
        for (i in 0..3) codigo[i] = ""
    }

    private fun iniciarCountdown() {
        countdownJob?.cancel()
        tiempoRestante = 300
        countdownJob = viewModelScope.launch {
            while (tiempoRestante > 0) {
                delay(1000L)
                tiempoRestante--
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
