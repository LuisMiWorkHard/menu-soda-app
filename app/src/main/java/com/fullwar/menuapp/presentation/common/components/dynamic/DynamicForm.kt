package com.fullwar.menuapp.presentation.common.components.dynamic

interface DynamicForm {
    var formFields: DynamicFormState

    fun updateField(key: String, value: Any?) {
        formFields = formFields.copy(
            fields = formFields.fields.toMutableMap().apply {
                put(key, value)
            }
        )
    }

    fun validate(): Boolean

    /**
     * Manejo centralizado de errores de validación del servidor (422).
     * Mapea los nombres de campos del servidor a los del formulario usando [fieldMapping].
     *
     * @param validationErrors Mapa de errores del servidor (campo → lista de mensajes)
     * @param fieldMapping Mapa de nombres: clave = nombre del servidor en lowercase, valor = nombre del formulario
     */
    fun handleValidationErrors(
        validationErrors: Map<String, List<String>>,
        fieldMapping: Map<String, String>
    ) {
        val serverErrors = mutableMapOf<String, String?>()
        validationErrors.forEach { (serverField, messages) ->
            val formField = fieldMapping[serverField.lowercase()] ?: serverField
            serverErrors[formField] = messages.firstOrNull()
        }
        formFields = formFields.copy(serverErrors = serverErrors)
    }
}