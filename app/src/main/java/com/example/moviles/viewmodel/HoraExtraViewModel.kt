package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CupoHoraExtra(
    val id: Int,
    val fecha: String,
    val horario: String,
    val horas: Int,
    val motivo: String,
    val estado: String
)

data class HoraExtraUiState(
    val horasAprobadasMes: Int = 12,
    val cuposDisponibles: List<CupoHoraExtra> = emptyList(),
    val mensaje: String? = null
)

class HoraExtraViewModel(application: Application) : AndroidViewModel(application) {

    private val horaExtraDao = AppDatabase.getDatabase(application).horaExtraDao()

    private val _uiState = MutableStateFlow(HoraExtraUiState())
    val uiState: StateFlow<HoraExtraUiState> = _uiState.asStateFlow()

    // Guardamos copias en memoria para actualizar la UI fácilmente
    private var listaEntitiesGuardada: List<CupoHoraExtraEntity> = emptyList()
    private val estadosLocales = mutableMapOf<Int, String>()

    init {
        // Escuchamos la base de datos de Room en tiempo real
        viewModelScope.launch {
            horaExtraDao.obtenerTodosLosCupos().collect { entidades ->
                listaEntitiesGuardada = entidades
                actualizarEstadoUi()
            }
        }
    }

    private fun actualizarEstadoUi(mensaje: String? = _uiState.value.mensaje) {
        val listaUi = listaEntitiesGuardada.map { entity ->
            val estadoActual = estadosLocales[entity.id] ?: "Disponible"
            CupoHoraExtra(
                id = entity.id,
                fecha = entity.fecha,
                horario = "${entity.horas} hrs extras disponibles",
                horas = entity.horas,
                motivo = entity.tarea,
                estado = estadoActual
            )
        }

        _uiState.value = HoraExtraUiState(
            horasAprobadasMes = 12,
            cuposDisponibles = listaUi,
            mensaje = mensaje
        )
    }

    fun solicitarCupo(cupoId: Int) {
        estadosLocales[cupoId] = "Pendiente"
        actualizarEstadoUi(mensaje = "Solicitud enviada a revisión por el Jefe de Obra.")
    }

    fun cancelarSolicitud(cupoId: Int) {
        estadosLocales[cupoId] = "Disponible"
        actualizarEstadoUi(mensaje = "Solicitud cancelada.")
    }

    fun mensajeConsumido() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }
}