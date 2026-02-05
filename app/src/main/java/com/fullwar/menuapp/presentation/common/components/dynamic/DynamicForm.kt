package com.fullwar.menuapp.presentation.common.components.dynamic

interface DynamicForm {
    var formFields: DynamicFormState

    fun updateField(key: String, value: Any?){
        formFields = formFields.copy(
            fields = formFields.fields.toMutableMap().apply {
                put(key, value)
            }
        )
    }

    fun validate(): Boolean
}