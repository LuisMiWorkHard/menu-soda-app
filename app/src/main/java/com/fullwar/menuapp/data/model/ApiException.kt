package com.fullwar.menuapp.data.model

class ApiException(
    val statusCode: Int,
    val errorDetail: String?,
    val errorCode: String?,
    val validationErrors: Map<String, List<String>>? = null
) : Exception(errorDetail ?: "Error desconocido del servidor (código $statusCode)")
