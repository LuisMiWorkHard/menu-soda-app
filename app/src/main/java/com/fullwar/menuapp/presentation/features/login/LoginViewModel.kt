package com.fullwar.menuapp.presentation.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepositoryImpl
) : ViewModel(), DynamicForm {

    val tiposDocumento: List<TipoDocumento> = listOf(
        TipoDocumento(tipoDocumento = 1, descripcionDocumento = "DNI"),
        TipoDocumento(tipoDocumento = 2, descripcionDocumento = "CE"),
        TipoDocumento(tipoDocumento = 3, descripcionDocumento = "RUC")
    )

    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "TipoDocumento" to tiposDocumento[0],
                "numeroDocumento" to TextFieldValue(),
                "contrasena" to TextFieldValue()
            )
        )
    )

    var loginState by mutableStateOf<State<Unit>>(State.Initial)
        private set

    fun login() {
        if (!validate()) return

        val tipoDoc = formFields.fields["TipoDocumento"] as TipoDocumento
        val numDoc = (formFields.fields["numeroDocumento"] as TextFieldValue).text
        val password = (formFields.fields["contrasena"] as TextFieldValue).text

        val request = LoginRequestDto(
            tipoDocumento = tipoDoc.tipoDocumento,
            numeroDocumento = numDoc,
            contrasena = password
        )

        viewModelScope.launch {
            loginState = State.Loading
            try {
                authRepository.loginAsync(request)
                loginState = State.Success(Unit)
            } catch (e: Exception) {
                loginState = State.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    override fun validate(): Boolean {
        val errors = mutableMapOf<String, Int?>()
        val tipoDoc = formFields.fields["TipoDocumento"] as? TipoDocumento
        val numDoc = (formFields.fields["numeroDocumento"] as? TextFieldValue)?.text ?: ""
        val password = (formFields.fields["contrasena"] as? TextFieldValue)?.text ?: ""

        if (tipoDoc == null) {
            errors["TipoDocumento"] = R.string.error_select_document_type
        } else {
            when (tipoDoc.tipoDocumento) {
                1 -> {
                    if (!numDoc.matches(Regex("^\\d{8}$"))) {
                        errors["numeroDocumento"] = R.string.error_dni_length
                    }
                }
                2 -> {
                    if (!numDoc.matches(Regex("^[a-zA-Z0-9]{12}$"))) {
                        errors["numeroDocumento"] = R.string.error_ce_length
                    }
                }
                3 -> {
                    if (!numDoc.matches(Regex("^\\d{12}$"))) {
                        errors["numeroDocumento"] = R.string.error_ruc_length
                    }
                }
            }
        }

        if (numDoc.isBlank()) {
            errors["numeroDocumento"] = R.string.error_empty_document_number
        }

        if (password.isBlank()) {
            errors["contrasena"] = R.string.error_empty_password
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }
}