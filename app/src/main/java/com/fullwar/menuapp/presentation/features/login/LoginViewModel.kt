package com.fullwar.menuapp.presentation.features.login

import androidx.lifecycle.ViewModel
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel() : ViewModel() {
    private val _tiposDocumento = MutableStateFlow<State<List<TipoDocumento>>>(State.Success(
        listOf(
            TipoDocumento(tipoDocumento = 1, descripcionDocumento = "DNI"),
            TipoDocumento(tipoDocumento = 2, descripcionDocumento = "CE"),
            TipoDocumento(tipoDocumento = 3, descripcionDocumento = "RUC")
        )
    ))
    val tiposDocumento: StateFlow<State<List<TipoDocumento>>> = _tiposDocumento
}