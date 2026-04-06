package com.fullwar.menuapp.presentation.features.home.tabs.nuevo.entrada

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.R
import com.fullwar.menuapp.data.model.ApiException
import com.fullwar.menuapp.data.model.EntradaCreateRequestDto
import com.fullwar.menuapp.data.model.EntradaCreateResponseDto
import com.fullwar.menuapp.data.model.EntradaResponseDto
import com.fullwar.menuapp.domain.repository.IEntradaRepository
import com.fullwar.menuapp.data.util.ImageCompressor
import com.fullwar.menuapp.domain.model.TipoEntrada
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class EntradaViewModel(
    private val entradaRepository: IEntradaRepository
) : ViewModel(), DynamicForm {

    companion object {
        private const val TAG = "EntradaViewModel"
        private val FIELD_MAPPING = mapOf(
            "entdes" to "entdes",
            "entdeslar" to "entdeslar",
            "codtipent" to "codtipent",
            "codima" to "codima"
        )
    }

    // --- Form state (DynamicForm) ---
    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "entdes" to TextFieldValue(),
                "entdeslar" to TextFieldValue(),
                "codtipent" to null, // TipoEntrada seleccionado
                "imageUri" to null   // Uri de imagen seleccionada (local)
            )
        )
    )

    // --- Async states ---
    var createState by mutableStateOf<State<EntradaCreateResponseDto>>(State.Initial)
        private set

    var tiposEntradaState by mutableStateOf<State<List<TipoEntrada>>>(State.Initial)
        private set

    var entradasState by mutableStateOf<State<List<EntradaResponseDto>>>(State.Initial)
        private set

    // --- Load tipos de entrada para los chips ---
    fun loadTiposEntrada() {
        if (tiposEntradaState is State.Success) return
        viewModelScope.launch {
            tiposEntradaState = State.Loading
            try {
                val dtos = entradaRepository.getTiposEntrada()
                val tipos = dtos
                    .filter { it.estadoId == 1 }
                    .map { TipoEntrada(id = it.id, descripcion = it.descripcion) }
                tiposEntradaState = State.Success(tipos)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tipos entrada", e)
                tiposEntradaState = State.Error(e.message ?: "Error cargando tipos")
            }
        }
    }

    // --- Load entradas list ---
    fun loadEntradas() {
        viewModelScope.launch {
            entradasState = State.Loading
            try {
                val entradas = entradaRepository.getEntradas()
                entradasState = State.Success(entradas)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading entradas", e)
                entradasState = State.Error(e.message ?: "Error cargando entradas")
            }
        }
    }

    // --- Crear nueva entrada ---
    fun createEntrada(context: Context) {
        if (!validate()) return

        val nombre = (formFields.fields["entdes"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["entdeslar"] as? TextFieldValue)?.text?.trim()
        val tipo = formFields.fields["codtipent"] as TipoEntrada
        val imageUri = formFields.fields["imageUri"] as? Uri

        viewModelScope.launch {
            createState = State.Loading
            try {
                // Si hay foto, subir primero
                var codima: Int? = null
                if (imageUri != null) {
                    val compressedBytes = ImageCompressor.compress(context, imageUri)
                    val uploadResponse = entradaRepository.uploadImage(
                        imageBytes = compressedBytes,
                        fileName = "entrada_${System.currentTimeMillis()}",
                        extension = ".jpg"
                    )
                    codima = uploadResponse.id
                }

                val request = EntradaCreateRequestDto(
                    descripcion = nombre,
                    descripcionLarga = descripcion?.ifBlank { null } ?: "",
                    tipoEntradaId = tipo.id,
                    imagenId = codima
                )

                val response = entradaRepository.createEntrada(request)
                loadEntradas()
                createState = State.Success(response)
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException creating entrada: ${e.message}")
                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    createState = State.Initial
                } else {
                    createState = State.Error(e.message ?: "Error al crear entrada")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating entrada", e)
                createState = State.Error(e.message ?: "Error al crear entrada")
            }
        }
    }

    // --- Validación cliente (replica FluentValidation del backend) ---
    override fun validate(): Boolean {
        formFields = formFields.copy(serverErrors = emptyMap())
        val errors = mutableMapOf<String, Int?>()

        val nombre = (formFields.fields["entdes"] as? TextFieldValue)?.text ?: ""
        val descripcion = (formFields.fields["entdeslar"] as? TextFieldValue)?.text ?: ""
        val tipo = formFields.fields["codtipent"] as? TipoEntrada

        // entdes: NotEmpty, MinLength(3), MaxLength(200)
        when {
            nombre.isBlank() -> errors["entdes"] = R.string.error_entrada_nombre_vacio
            nombre.trim().length < 3 -> errors["entdes"] = R.string.error_entrada_nombre_min
            nombre.trim().length > 200 -> errors["entdes"] = R.string.error_entrada_nombre_max
        }

        // entdeslar: MaxLength(1000) - optional
        if (descripcion.isNotBlank() && descripcion.trim().length > 1000) {
            errors["entdeslar"] = R.string.error_entrada_descripcion_max
        }

        // codtipent: NotEmpty, GreaterThan(0)
        if (tipo == null) {
            errors["codtipent"] = R.string.error_entrada_tipo_vacio
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }

    fun resetForm() {
        formFields = DynamicFormState(
            fields = mapOf(
                "entdes" to TextFieldValue(),
                "entdeslar" to TextFieldValue(),
                "codtipent" to null,
                "imageUri" to null
            )
        )
        createState = State.Initial
    }
}
