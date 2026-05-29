package com.fullwar.menuapp.presentation.features.home.tabs.perfil.cambiar_contrasena

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.data.model.CambiarContrasenaRequestDto
import com.fullwar.menuapp.domain.repository.IUsuarioRepository
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class CambiarContrasenaViewModel(
    private val repo: IUsuarioRepository
) : ViewModel(), DynamicForm {

    companion object {
        private val FIELD_MAPPING = mapOf(
            "contrasenaactual" to "contrasenaActual",
            "contraseñanueva"  to "contrasenaNueva",
            "contrasenanúeva"  to "contrasenaNueva"
        )
    }

    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "contrasenaActual"    to TextFieldValue(""),
                "contrasenaNueva"     to TextFieldValue(""),
                "confirmarContrasena" to TextFieldValue("")
            )
        )
    )

    var cambiarState by mutableStateOf<State<Unit>>(State.Initial)
        private set

    fun resetState() {
        cambiarState = State.Initial
    }

    fun cambiarContrasena() {
        if (!validate()) return
        val actual = (formFields.fields["contrasenaActual"] as? TextFieldValue)?.text ?: ""
        val nueva  = (formFields.fields["contrasenaNueva"]  as? TextFieldValue)?.text ?: ""
        viewModelScope.launch {
            cambiarState = State.Loading
            try {
                repo.cambiarContrasena(CambiarContrasenaRequestDto(actual, nueva))
                cambiarState = State.Success(Unit)
            } catch (e: ApiException) {
                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    cambiarState = State.Initial
                } else {
                    cambiarState = State.Error(e.message ?: "Error al cambiar contraseña")
                }
            } catch (e: Exception) {
                cambiarState = State.Error(e.message ?: "Error al cambiar contraseña")
            }
        }
    }

    override fun validate(): Boolean {
        formFields = formFields.copy(serverErrors = emptyMap())
        val errors = mutableMapOf<String, Int?>()
        val actual = (formFields.fields["contrasenaActual"]    as? TextFieldValue)?.text ?: ""
        val nueva  = (formFields.fields["contrasenaNueva"]     as? TextFieldValue)?.text ?: ""
        val conf   = (formFields.fields["confirmarContrasena"] as? TextFieldValue)?.text ?: ""

        if (actual.isBlank()) {
            errors["contrasenaActual"] = R.string.error_contrasena_actual_vacia
        }
        if (nueva.isBlank()) {
            errors["contrasenaNueva"] = R.string.error_contrasena_nueva_vacia
        } else if (nueva.length < 8) {
            errors["contrasenaNueva"] = R.string.error_invalid_password
        }
        if (nueva.isNotBlank() && conf != nueva) {
            errors["confirmarContrasena"] = R.string.error_contrasenas_no_coinciden
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }
}
