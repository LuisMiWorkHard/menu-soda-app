package com.fullwar.menuapp.presentation.features.login

import androidx.lifecycle.ViewModel
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel() : ViewModel() {
    val tiposDocumento: List<TipoDocumento> = listOf(
        TipoDocumento(tipoDocumento = 1, descripcionDocumento = "DNI"),
        TipoDocumento(tipoDocumento = 2, descripcionDocumento = "CE"),
        TipoDocumento(tipoDocumento = 3, descripcionDocumento = "RUC")
    )
}