package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import com.example.moviles.data.local.database.entity.SolicitudHoraExtraEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val db = AppDatabase.getDatabase(application)
    private val horaExtraDao = db.horaExtraDao()
    private val solicitudDao = db.solicitudHoraExtraDao()

    private val _uiState = MutableStateFlow(HoraExtraUiState())
    val uiState: StateFlow<HoraExtraUiState> = _uiState.asStateFlow()

    private var cuposPublicados: List<CupoHoraExtraEntity> = emptyList()

    init {
        // Escuchamos en tiempo real los cupos y las solicitudes registradas en Room
        viewModelScope.launch {
            combine(
                horaExtraDao.obtenerTodosLosCupos(),
                solicitudDao.obtenerTodasLasSolicitudes()
            ) { cupos, solicitudes ->
                cuposPublicados = cupos
                val solicitudesMapa = solicitudes.associateBy { it.cupoId }

                cupos.map { entity ->
                    val estadoActual = solicitudesMapa[entity.id]?.estado ?: "Disponible"
                    CupoHoraExtra(
                        id = entity.id,
                        fecha = entity.fecha,
                        horario = "${entity.horas} hrs extras disponibles",
                        horas = entity.horas,
                        motivo = entity.tarea,
                        estado = estadoActual
                    )
                }
            }.collect { listaUi ->
                _uiState.value = _uiState.value.copy(cuposDisponibles = listaUi)
            }
        }
    }

    fun solicitarCupo(cupoId: Int) {
        val cupo = cuposPublicados.find { it.id == cupoId } ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val nuevaSolicitud = SolicitudHoraExtraEntity(
                cupoId = cupo.id,
                trabajadorNombre = "Vania Valdés",
                tarea = cupo.tarea,
                horas = cupo.horas,
                fecha = cupo.fecha,
                estado = "Pendiente"
            )
            solicitudDao.insertarSolicitud(nuevaSolicitud)
        }
        _uiState.value = _uiState.value.copy(mensaje = "Solicitud enviada a revisión por el Jefe de Obra.")
    }

    fun cancelarSolicitud(cupoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.eliminarPorCupoId(cupoId)
        }
        _uiState.value = _uiState.value.copy(mensaje = "Solicitud cancelada.")
    }

    fun mensajeConsumido() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }
}