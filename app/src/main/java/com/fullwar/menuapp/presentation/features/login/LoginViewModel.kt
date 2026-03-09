package com.fullwar.menuapp.presentation.features.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.data.model.LoginRequestDto
import com.fullwar.menuapp.data.repository.AuthRepositoryImpl
import com.fullwar.menuapp.data.repository.LocationProviderImpl
import com.fullwar.menuapp.domain.model.TipoDocumento
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepositoryImpl,
    private val locationProvider: LocationProviderImpl
) : ViewModel(), DynamicForm {

    companion object {
        private const val TAG = "LoginViewModel"
        private val FIELD_MAPPING = mapOf(
            "contrasena" to "contrasena",
            "numerodocumento" to "numeroDocumento",
            "tipodocumento" to "TipoDocumento"
        )
    }

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

    var ubicacionPermitida by mutableStateOf(false)
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
            val totalStartTime = System.currentTimeMillis()
            loginState = State.Loading

            try {
                // Si la ubicación no está cacheada, obtenerla ahora
                if (locationProvider.getLatitude() == null && ubicacionPermitida) {
                    Log.d(TAG, "Location not cached, fetching now...")
                    val locationStartTime = System.currentTimeMillis()
                    locationProvider.actualizarUbicacion(true)
                    val locationElapsed = System.currentTimeMillis() - locationStartTime
                    Log.d(TAG, "Location fetched in ${locationElapsed}ms")
                }

                if (locationProvider.getLatitude() == null || locationProvider.getLongitude() == null) {
                    loginState = State.Error("Se requiere permiso de ubicación para iniciar sesión")
                    return@launch
                }

                Log.d(TAG, "Starting login request...")
                val loginStartTime = System.currentTimeMillis()
                authRepository.loginAsync(request)
                val loginElapsed = System.currentTimeMillis() - loginStartTime
                Log.d(TAG, "Login request completed in ${loginElapsed}ms")

                val totalElapsed = System.currentTimeMillis() - totalStartTime
                Log.d(TAG, "Total login flow completed in ${totalElapsed}ms")

                loginState = State.Success(Unit)
            } catch (e: ApiException) {
                val totalElapsed = System.currentTimeMillis() - totalStartTime
                Log.e(TAG, "Login failed after ${totalElapsed}ms: ${e.message}")

                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    loginState = State.Initial
                } else {
                    loginState = State.Error(e.message ?: "Error al iniciar sesión")
                }
            } catch (e: Exception) {
                val totalElapsed = System.currentTimeMillis() - totalStartTime
                Log.e(TAG, "Login failed after ${totalElapsed}ms: ${e.message}", e)
                loginState = State.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun onLocationPermisoResultado(concedido: Boolean) {
        ubicacionPermitida = concedido
        if (concedido) {
            // Pre-cargar ubicación cuando se concede el permiso
            viewModelScope.launch {
                Log.d(TAG, "Location permission granted, pre-loading...")
                val startTime = System.currentTimeMillis()
                locationProvider.actualizarUbicacion(true)
                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "Location pre-loaded after permission in ${elapsed}ms")
            }
        }
    }

    override fun validate(): Boolean {
        // Limpiar errores del servidor al re-validar
        formFields = formFields.copy(serverErrors = emptyMap())

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