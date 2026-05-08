package com.fullwar.menuapp.presentation.features.home.tabs.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto
import com.fullwar.menuapp.domain.repository.IMenuDiarioRepository
import com.fullwar.menuapp.presentation.common.utils.State
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

class HistorialViewModel(private val repo: IMenuDiarioRepository) : ViewModel() {

    var menusState by mutableStateOf<State<List<MenuDiarioListItemResponseDto>>>(State.Initial)
        private set

    var displayMenus by mutableStateOf<List<MenuDiarioListItemResponseDto>>(emptyList())
        private set

    private var allMenus: List<MenuDiarioListItemResponseDto> = emptyList()
    private var currentDateFilter: DateFilter = DateFilter.None

    fun loadMenus(busqueda: String? = null) {
        viewModelScope.launch {
            menusState = State.Loading
            try {
                val menus = repo.getMenusDiarios(busqueda)
                    .map { it.copy(descripcionFecha = computeDescripcionFecha(it.fecha)) }
                allMenus = menus
                applyDateFilter()
                menusState = State.Success(menus)
            } catch (e: Exception) {
                menusState = State.Error(e.message ?: "Error cargando historial")
            }
        }
    }

    private fun computeDescripcionFecha(fechaStr: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val menuDate = LocalDate.parse(fechaStr, formatter)
            val today = LocalDate.now()
            when (menuDate) {
                today -> "Hoy"
                today.minusDays(1) -> "Ayer"
                today.plusDays(1) -> "Mañana"
                else -> {
                    val startOfWeekToday = today.with(DayOfWeek.MONDAY)
                    val startOfWeekMenu = menuDate.with(DayOfWeek.MONDAY)
                    val dayName = when (menuDate.dayOfWeek) {
                        DayOfWeek.MONDAY -> "Lunes"; DayOfWeek.TUESDAY -> "Martes"
                        DayOfWeek.WEDNESDAY -> "Miércoles"; DayOfWeek.THURSDAY -> "Jueves"
                        DayOfWeek.FRIDAY -> "Viernes"; DayOfWeek.SATURDAY -> "Sábado"
                        else -> "Domingo"
                    }
                    if (startOfWeekToday == startOfWeekMenu) dayName
                    else {
                        val month = listOf("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic")[menuDate.monthValue - 1]
                        "$dayName, ${menuDate.dayOfMonth.toString().padStart(2, '0')} $month ${menuDate.year}"
                    }
                }
            }
        } catch (_: Exception) { fechaStr }
    }

    fun searchMenus(query: String) = loadMenus(busqueda = query.trim().takeIf { it.isNotBlank() })

    fun resetSearch() = loadMenus(busqueda = null)

    fun filterByDate(selectedMillis: Long?) {
        currentDateFilter = if (selectedMillis != null) DateFilter.Single(selectedMillis) else DateFilter.None
        applyDateFilter()
    }

    fun filterByDateRange(startMillis: Long?, endMillis: Long?) {
        currentDateFilter = if (startMillis != null && endMillis != null)
            DateFilter.Range(startMillis, endMillis)
        else
            DateFilter.None
        applyDateFilter()
    }

    fun clearDateFilter() {
        currentDateFilter = DateFilter.None
        applyDateFilter()
    }

    fun eliminarMenu(id: Int) {
        viewModelScope.launch {
            runCatching { repo.deleteMenuDiario(id) }
                .onSuccess { loadMenus() }
                .onFailure { /* el error se maneja silenciosamente; el refresh fallido no cambia el estado */ }
        }
    }

    private fun applyDateFilter() {
        displayMenus = when (val f = currentDateFilter) {
            is DateFilter.None -> allMenus
            is DateFilter.Single -> allMenus.filter { parseFechaToMillis(it.fecha) == f.millis }
            is DateFilter.Range -> allMenus.filter {
                val t = parseFechaToMillis(it.fecha)
                t in f.startMillis..f.endMillis
            }
        }
    }

    private fun parseFechaToMillis(fecha: String): Long {
        val parts = fecha.split("/")
        if (parts.size != 3) return 0L
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.clear()
        cal.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt(), 0, 0, 0)
        return cal.timeInMillis
    }

    sealed class DateFilter {
        object None : DateFilter()
        data class Single(val millis: Long) : DateFilter()
        data class Range(val startMillis: Long, val endMillis: Long) : DateFilter()
    }
}