package com.fullwar.menuapp.presentation.common.utils

sealed class State<out T: Any> {
    data object Initial: State<Nothing>()
    data object Loading: State<Nothing>()
    data class Success<out T: Any>(val data: T) : State<T>()
    data class Error(val message: String) : State<Nothing>()
}