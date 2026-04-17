package com.fullwar.menuapp.presentation.features.menu.entrada.gestion.shared

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
import com.fullwar.menuapp.data.model.EntradaUpdateRequestDto
import com.fullwar.menuapp.data.util.ImageCompressor
import com.fullwar.menuapp.domain.model.TipoEntrada
import com.fullwar.menuapp.domain.repository.IEntradaRepository
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
            "entnom" to "entnom",
            "entdes" to "entdes",
            "codtipent" to "codtipent",
            "codima" to "codima"
        )
    }

    // --- Modo editar ---
    private var editingId: Int? = null
    private var originalImagenId: Int? = null

    var currentImagenId: Int? by mutableStateOf(null)
        private set

    // --- Form state (DynamicForm) ---
    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "entnom" to TextFieldValue(),
                "entdes" to TextFieldValue(),
                "codtipent" to null,
                "imageUri" to null
            )
        )
    )

    // --- Async states ---
    var createState by mutableStateOf<State<EntradaCreateResponseDto>>(State.Initial)
        private set

    var editState by mutableStateOf<State<EntradaResponseDto>>(State.Initial)
        private set

    var tiposEntradaState by mutableStateOf<State<List<TipoEntrada>>>(State.Initial)
        private set

    var entradasState by mutableStateOf<State<List<EntradaResponseDto>>>(State.Initial)
        private set

    var searchResults by mutableStateOf<List<EntradaResponseDto>>(emptyList())
        private set

    var duplicateMatches by mutableStateOf<List<EntradaResponseDto>>(emptyList())
        private set

    val isEditMode: Boolean get() = editingId != null

    // --- Inicializar modo ---

    fun initForCreate() {
        editingId = null
        originalImagenId = null
        currentImagenId = null
        createState = State.Initial
        resetForm()
    }

    fun initForEdit(entrada: EntradaResponseDto) {
        editingId = entrada.id
        originalImagenId = entrada.imagenId
        currentImagenId = entrada.imagenId
        editState = State.Initial
        formFields = DynamicFormState(
            fields = mapOf(
                "entnom" to TextFieldValue(entrada.nombre),
                "entdes" to TextFieldValue(entrada.descripcion),
                "codtipent" to TipoEntrada(id = entrada.tipoEntradaId, descripcion = ""),
                "imageUri" to null
            )
        )
    }

    // --- Guardar (delega según el modo) ---
    fun save(context: Context) {
        if (editingId != null) updateEntrada(context)
        else createEntrada(context)
    }

    // --- Load tipos de entrada para los chips ---
    fun loadTiposEntrada() {
        if (tiposEntradaState is State.Success<List<TipoEntrada>>) return
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

    // --- Search entradas (server-side fuzzy) ---
    fun searchEntradas(query: String) {
        viewModelScope.launch {
            try {
                searchResults = entradaRepository.searchEntradas(query)
            } catch (e: Exception) {
                Log.e(TAG, "Error searching entradas", e)
            }
        }
    }

    fun checkForDuplicates(nombre: String) {
        viewModelScope.launch {
            try {
                duplicateMatches = entradaRepository.findSimilarEntradas(nombre, editingId)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking duplicates", e)
            }
        }
    }

    fun clearDuplicateMatches() { duplicateMatches = emptyList() }

    fun resetSearch() {
        val state = entradasState
        if (state is State.Success) searchResults = state.data
    }

    // --- Load entradas list ---
    fun loadEntradas() {
        viewModelScope.launch {
            entradasState = State.Loading
            try {
                val entradas = entradaRepository.getEntradas()
                entradasState = State.Success(entradas)
                searchResults = entradas
            } catch (e: Exception) {
                Log.e(TAG, "Error loading entradas", e)
                entradasState = State.Error(e.message ?: "Error cargando entradas")
            }
        }
    }

    // --- Crear nueva entrada ---
    private fun createEntrada(context: Context) {
        if (!validate()) return

        val nombre = (formFields.fields["entnom"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["entdes"] as? TextFieldValue)?.text?.trim()
        val tipo = formFields.fields["codtipent"] as TipoEntrada
        val imageUri = formFields.fields["imageUri"] as? Uri

        viewModelScope.launch {
            createState = State.Loading
            try {
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
                    nombre = nombre,
                    descripcion = descripcion?.ifBlank { null } ?: "",
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

    // --- Actualizar entrada existente ---
    private fun updateEntrada(context: Context) {
        if (!validate()) return

        val id = editingId ?: return
        val nombre = (formFields.fields["entnom"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["entdes"] as? TextFieldValue)?.text?.trim()
        val tipo = formFields.fields["codtipent"] as TipoEntrada
        val imageUri = formFields.fields["imageUri"] as? Uri

        viewModelScope.launch {
            editState = State.Loading
            try {
                var codima: Int? = originalImagenId
                if (imageUri != null) {
                    val compressedBytes = ImageCompressor.compress(context, imageUri)
                    val uploadResponse = entradaRepository.uploadImage(
                        imageBytes = compressedBytes,
                        fileName = "entrada_${System.currentTimeMillis()}",
                        extension = ".jpg"
                    )
                    codima = uploadResponse.id
                }

                val request = EntradaUpdateRequestDto(
                    nombre = nombre,
                    descripcion = descripcion?.ifBlank { null } ?: "",
                    tipoEntradaId = tipo.id,
                    imagenId = codima
                )

                val response = entradaRepository.updateEntrada(id, request)
                loadEntradas()
                editState = State.Success(response)
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException updating entrada: ${e.message}")
                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    editState = State.Initial
                } else {
                    editState = State.Error(e.message ?: "Error al actualizar entrada")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating entrada", e)
                editState = State.Error(e.message ?: "Error al actualizar entrada")
            }
        }
    }

    // --- Validación cliente ---
    override fun validate(): Boolean {
        formFields = formFields.copy(serverErrors = emptyMap())
        val errors = mutableMapOf<String, Int?>()

        val nombre = (formFields.fields["entnom"] as? TextFieldValue)?.text ?: ""
        val descripcion = (formFields.fields["entdes"] as? TextFieldValue)?.text ?: ""
        val tipo = formFields.fields["codtipent"] as? TipoEntrada

        when {
            nombre.isBlank() -> errors["entnom"] = R.string.error_entrada_nombre_vacio
            nombre.trim().length < 3 -> errors["entnom"] = R.string.error_entrada_nombre_min
            nombre.trim().length > 200 -> errors["entnom"] = R.string.error_entrada_nombre_max
        }

        if (descripcion.isNotBlank() && descripcion.trim().length > 1000) {
            errors["entdes"] = R.string.error_entrada_descripcion_max
        }

        if (tipo == null) {
            errors["codtipent"] = R.string.error_entrada_tipo_vacio
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }

    fun resetForm() {
        formFields = DynamicFormState(
            fields = mapOf(
                "entnom" to TextFieldValue(),
                "entdes" to TextFieldValue(),
                "codtipent" to null,
                "imageUri" to null
            )
        )
        createState = State.Initial
        editState = State.Initial
        currentImagenId = null
    }
}
