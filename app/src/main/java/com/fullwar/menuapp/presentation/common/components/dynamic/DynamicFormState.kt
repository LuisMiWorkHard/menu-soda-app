package com.fullwar.menuapp.presentation.common.components.dynamic

data class DynamicFormState(
    val fields: Map<String, Any?> = emptyMap(),
    val errors: Map<String, Int?> = emptyMap()
)