package com.fullwar.menuapp.presentation.features.menu.plato

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
            "tipoplatoid" to "codtippla"
        )
    }

    // --- Form state (DynamicForm) ---
    override var formFields by mutableStateOf(
        DynamicFormState(
            fields = mapOf(
                "platnom" to TextFieldValue(),
                "platdes" to TextFieldValue(),
                "codtippla" to null
            )
        )
    )

    // --- Async states ---
    var platosState by mutableStateOf<State<List<PlatoResponseDto>>>(State.Initial)
        private set

    var createState by mutableStateOf<State<PlatoCreateResponseDto>>(State.Initial)
        private set

    var tiposPlatoState by mutableStateOf<State<List<TipoPlato>>>(State.Initial)
        private set

    // --- Inicializar modo crear ---
    fun initForCreate() {
        createState = State.Initial
        formFields = DynamicFormState(
            fields = mapOf(
                "platnom" to TextFieldValue(),
                "platdes" to TextFieldValue(),
                "codtippla" to null
            )
        )
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

    // --- Guardar nuevo plato ---
    fun save() {
        if (!validate()) return

        val nombre = (formFields.fields["platnom"] as TextFieldValue).text.trim()
        val descripcion = (formFields.fields["platdes"] as TextFieldValue).text.trim()
        val tipo = formFields.fields["codtippla"] as TipoPlato

        viewModelScope.launch {
            createState = State.Loading
            try {
                val request = PlatoCreateRequestDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    tipoPlatoId = tipo.id
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
}
