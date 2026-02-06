package com.fullwar.menuapp.presentation.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState


class LoginViewModel() : ViewModel(), DynamicForm {
    val tiposDocumento: List<TipoDocumento> = listOf(
        TipoDocumento(tipoDocumento = 1, descripcionDocumento = "DNI"),
        TipoDocumento(tipoDocumento = 2, descripcionDocumento = "CE"),
        TipoDocumento(tipoDocumento = 3, descripcionDocumento = "RUC")
    )
    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "TipoDocumento" to (null as TipoDocumento?),
                "numeroDocumento" to TextFieldValue(),
                "contrasena" to TextFieldValue()
            )
        )
    )

    override fun validate(): Boolean {
        val errors = mutableMapOf<String, String>()
        val tipoDoc = formFields.fields["TipoDocumento"] as? TipoDocumento
        val numDoc = (formFields.fields["numeroDocumento"] as? TextFieldValue)?.text ?: ""
        val password = (formFields.fields["contrasena"] as? TextFieldValue)?.text ?: ""

        if (tipoDoc == null) {
            errors["TipoDocumento"] = "Seleccione un tipo de documento"
        } else {
            when (tipoDoc.tipoDocumento) {
                1 -> { // DNI
                    if (!numDoc.matches(Regex("^\\d{8}$"))) {
                        errors["numeroDocumento"] = "El DNI deben ser 8 dígitos exactos"
                    }
                }
                2 -> { // CE
                    if (!numDoc.matches(Regex("^[a-zA-Z0-0]{12}$"))) {
                        errors["numeroDocumento"] = "El CE deben ser 12 caracteres alfanuméricos"
                    }
                }
                3 -> { // RUC
                    if (!numDoc.matches(Regex("^\\d{12}$"))) {
                        errors["numeroDocumento"] = "El RUC deben ser 12 dígitos exactos"
                    }
                }
            }
        }

        if (numDoc.isBlank()) {
            errors["numeroDocumento"] = "Ingrese su número de documento"
        }

        if (password.isBlank()) {
            errors["contrasena"] = "Ingrese su contraseña"
        } else {
            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
            if (!passwordRegex.matches(password)) {
                errors["contrasena"] = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
            }
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }
}