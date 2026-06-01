package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.remote.RecuperarContrasenaService
import com.fullwar.menuapp.data.model.RestablecerContrasenaRecuperacionRequestDto
import com.fullwar.menuapp.data.model.VerificarCodigoRecuperacionRequestDto
import com.fullwar.menuapp.domain.repository.IRecuperarContrasenaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecuperarContrasenaRepositoryImpl(
    private val service: RecuperarContrasenaService
) : IRecuperarContrasenaRepository {

    override suspend fun enviarCodigoRecuperacion(): String =
        withContext(Dispatchers.IO) {
            service.enviarCodigoRecuperacion().emailMasked
        }

    override suspend fun verificarCodigoRecuperacion(codigo: String) =
        withContext(Dispatchers.IO) {
            service.verificarCodigoRecuperacion(VerificarCodigoRecuperacionRequestDto(codigo))
        }

    override suspend fun restablecerContrasenaRecuperacion(nuevaContrasena: String, confirmarContrasena: String) =
        withContext(Dispatchers.IO) {
            service.restablecerContrasenaRecuperacion(
                RestablecerContrasenaRecuperacionRequestDto(nuevaContrasena, confirmarContrasena)
            )
        }
}
