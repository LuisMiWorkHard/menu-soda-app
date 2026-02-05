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
        if(formFields.fields["TipoDocumento"] == null) errors["TipoDocumento"] = "Seleccione un tipo de documento"
        if(formFields.fields["numeroDocumento"]?.let { (it as TextFieldValue).text.isBlank() } != false) errors["numeroDocumento"] = "Ingrese su número de documento"
        if(formFields.fields["contrasena"]?.let { (it as TextFieldValue).text.isBlank() } != false) errors["contrasena"] = "Ingrese su contraseña"

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }
}