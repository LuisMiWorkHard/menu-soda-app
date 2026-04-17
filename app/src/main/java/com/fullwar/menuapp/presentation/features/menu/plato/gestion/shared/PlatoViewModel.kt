package com.fullwar.menuapp.presentation.features.menu.plato.gestion.shared

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
import com.fullwar.menuapp.data.model.PlatoCreateRequestDto
import com.fullwar.menuapp.data.model.PlatoCreateResponseDto
import com.fullwar.menuapp.data.model.PlatoResponseDto
import com.fullwar.menuapp.data.model.PlatoUpdateRequestDto
import com.fullwar.menuapp.data.util.ImageCompressor
import com.fullwar.menuapp.domain.model.TipoPlato
import com.fullwar.menuapp.domain.repository.IPlatoRepository
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicForm
import com.fullwar.menuapp.presentation.common.components.dynamic.DynamicFormState
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch

class PlatoViewModel(private val repository: IPlatoRepository) : ViewModel(), DynamicForm {

    companion object {
        private const val TAG = "PlatoViewModel"
        private val FIELD_MAPPING = mapOf(
            "nombre" to "platnom",
            "descripcion" to "platdes",
            "tipoplatoid" to "codtippla",
            "imagenid" to "codima"
        )
    }

    // --- Modo editar ---
    private var editingId: Int? = null
    private var originalImagenId: Int? = null
    private var originalEstadoId: Int = 1

    var currentImagenId: Int? by mutableStateOf(null)
        private set

    // --- Form state (DynamicForm) ---
    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "platnom" to TextFieldValue(),
                "platdes" to TextFieldValue(),
                "codtippla" to null,
                "imageUri" to null
            )
        )
    )

    // --- Async states ---
    var platosState by mutableStateOf<State<List<PlatoResponseDto>>>(State.Initial)
        private set

    var createState by mutableStateOf<State<PlatoCreateResponseDto>>(State.Initial)
        private set

    var editState by mutableStateOf<State<Unit>>(State.Initial)
        private set

    var tiposPlatoState by mutableStateOf<State<List<TipoPlato>>>(State.Initial)
        private set

    // --- Inicializar modo crear ---
    fun initForCreate() {
        editingId = null
        originalImagenId = null
        originalEstadoId = 1
        currentImagenId = null
        createState = State.Initial
        resetForm()
    }

    // --- Inicializar modo editar ---
    fun initForEdit(plato: PlatoResponseDto) {
        editingId = plato.id
        originalImagenId = plato.imagenId
        originalEstadoId = plato.estadoId
        currentImagenId = plato.imagenId
        editState = State.Initial
        formFields = DynamicFormState(
            fields = mapOf(
                "platnom" to TextFieldValue(plato.nombre),
                "platdes" to TextFieldValue(plato.descripcion),
                "codtippla" to TipoPlato(id = plato.tipoPlatoId, descripcion = ""),
                "imageUri" to null
            )
        )
    }

    // --- Guardar (delega según el modo) ---
    fun save(context: Context) {
        if (editingId != null) updatePlato(context)
        else createPlato(context)
    }

    // --- Cargar lista de platos ---
    fun loadPlatos() {
        viewModelScope.launch {
            platosState = State.Loading
            platosState = try {
                State.Success(repository.getPlatos())
            } catch (e: Exception) {
                Log.e(TAG, "Error loading platos", e)
                State.Error(e.message ?: "Error al cargar platos")
            }
        }
    }

    // --- Cargar tipos de plato para los chips ---
    fun loadTiposPlato() {
        if (tiposPlatoState is State.Success<List<TipoPlato>>) return
        viewModelScope.launch {
            tiposPlatoState = State.Loading
            try {
                val dtos = repository.getTiposPlato()
                val tipos = dtos
                    .filter { it.estadoId == 1 }
                    .map { TipoPlato(id = it.id, descripcion = it.descripcion) }
                tiposPlatoState = State.Success(tipos)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading tipos plato", e)
                tiposPlatoState = State.Error(e.message ?: "Error cargando tipos")
            }
        }
    }

    // --- Crear nuevo plato ---
    private fun createPlato(context: Context) {
        if (!validate()) return

        val nombre = (formFields.fields["platnom"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["platdes"] as TextFieldValue).text.trim()
        val tipo = formFields.fields["codtippla"] as TipoPlato
        val imageUri = formFields.fields["imageUri"] as? Uri

        viewModelScope.launch {
            createState = State.Loading
            try {
                var codima: Int? = null
                if (imageUri != null) {
                    val compressedBytes = ImageCompressor.compress(context, imageUri)
                    val uploadResponse = repository.uploadImage(
                        imageBytes = compressedBytes,
                        fileName = "plato_${System.currentTimeMillis()}",
                        extension = ".jpg"
                    )
                    codima = uploadResponse.id
                }

                val request = PlatoCreateRequestDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    tipoPlatoId = tipo.id,
                    imagenId = codima
                )
                val response = repository.createPlato(request)
                loadPlatos()
                createState = State.Success(response)
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException creating plato: ${e.message}")
                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    createState = State.Initial
                } else {
                    createState = State.Error(e.message ?: "Error al crear plato")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating plato", e)
                createState = State.Error(e.message ?: "Error al crear plato")
            }
        }
    }

    // --- Actualizar plato existente ---
    private fun updatePlato(context: Context) {
        if (!validate()) return

        val id = editingId ?: return
        val nombre = (formFields.fields["platnom"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["platdes"] as TextFieldValue).text.trim()
        val tipo = formFields.fields["codtippla"] as TipoPlato
        val imageUri = formFields.fields["imageUri"] as? Uri

        viewModelScope.launch {
            editState = State.Loading
            try {
                var codima: Int? = originalImagenId
                if (imageUri != null) {
                    val compressedBytes = ImageCompressor.compress(context, imageUri)
                    val uploadResponse = repository.uploadImage(
                        imageBytes = compressedBytes,
                        fileName = "plato_${System.currentTimeMillis()}",
                        extension = ".jpg"
                    )
                    codima = uploadResponse.id
                }

                val request = PlatoUpdateRequestDto(
                    id = id,
                    nombre = nombre,
                    descripcion = descripcion,
                    tipoPlatoId = tipo.id,
                    estadoId = originalEstadoId,
                    imagenId = codima
                )
                repository.updatePlato(id, request)
                loadPlatos()
                editState = State.Success(Unit)
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException updating plato: ${e.message}")
                if (e.validationErrors != null) {
                    handleValidationErrors(e.validationErrors, FIELD_MAPPING)
                    editState = State.Initial
                } else {
                    editState = State.Error(e.message ?: "Error al actualizar plato")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating plato", e)
                editState = State.Error(e.message ?: "Error al actualizar plato")
            }
        }
    }

    // --- Validación cliente ---
    override fun validate(): Boolean {
        formFields = formFields.copy(serverErrors = emptyMap())
        val errors = mutableMapOf<String, Int?>()

        val nombre = (formFields.fields["platnom"] as? TextFieldValue)?.text ?: ""
        val descripcion = (formFields.fields["platdes"] as? TextFieldValue)?.text ?: ""
        val tipo = formFields.fields["codtippla"] as? TipoPlato

        when {
            nombre.isBlank() -> errors["platnom"] = R.string.error_plato_nombre_vacio
            nombre.trim().length > 100 -> errors["platnom"] = R.string.error_plato_nombre_max
        }

        when {
            descripcion.isBlank() -> errors["platdes"] = R.string.error_plato_descripcion_vacia
            descripcion.trim().length > 150 -> errors["platdes"] = R.string.error_plato_descripcion_max
        }

        if (tipo == null) {
            errors["codtippla"] = R.string.error_plato_tipo_vacio
        }

        formFields = formFields.copy(errors = errors)
        return errors.isEmpty()
    }

    fun resetForm() {
        formFields = DynamicFormState(
            fields = mapOf(
                "platnom" to TextFieldValue(),
                "platdes" to TextFieldValue(),
                "codtippla" to null,
                "imageUri" to null
            )
        )
        createState = State.Initial
        editState = State.Initial
        currentImagenId = null
    }
}
