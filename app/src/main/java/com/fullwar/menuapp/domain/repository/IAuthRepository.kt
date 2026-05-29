package com.fullwar.menuapp.domain.repository

interface IAuthRepository {
    suspend fun enviarCodigoRecuperacion(): String
    suspend fun verificarCodigoRecuperacion(codigo: String)
    suspend fun restablecerContrasenaRecuperacion(nuevaContrasena: String, confirmarContrasena: String)
}
