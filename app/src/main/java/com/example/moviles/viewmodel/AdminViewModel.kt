package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import com.example.moviles.data.local.database.entity.SolicitudHoraExtraEntity
import com.example.moviles.data.local.entity.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val horaExtraDao = db.horaExtraDao()
    private val solicitudDao = db.solicitudHoraExtraDao()

    val usuarios: StateFlow<List<UsuarioEntity>> = usuarioDao.obtenerTodosLosUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cuposPublicados: StateFlow<List<CupoHoraExtraEntity>> = horaExtraDao.obtenerTodosLosCupos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Escucha directa y reactiva de las solicitudes persistidas en Room
    val solicitudesHE: StateFlow<List<SolicitudHoraExtraEntity>> = solicitudDao.obtenerTodasLasSolicitudes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun publicarCupo(tarea: String, horasStr: String, cuposStr: String, fecha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val horas = horasStr.toIntOrNull() ?: 1
            val cupos = cuposStr.toIntOrNull() ?: 1

            val nuevoCupo = CupoHoraExtraEntity(
                tarea = tarea,
                horas = horas,
                cuposTotales = cupos,
                cuposDisponibles = cupos,
                fecha = fecha,
                obra = "Costanera Edificio B"
            )
            horaExtraDao.insertarCupo(nuevoCupo)
        }
    }

    fun aprobarSolicitud(cupoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.actualizarEstado(cupoId, "Aprobada")
        }
    }

    fun rechazarSolicitud(cupoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudDao.actualizarEstado(cupoId, "Rechazada")
        }
    }

    fun eliminarUsuario(usuarioId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            usuarioDao.eliminarUsuarioPorId(usuarioId)
        }
    }

    fun eliminarCupo(cupoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            horaExtraDao.eliminarCupo(cupoId)
            solicitudDao.eliminarPorCupoId(cupoId)
        }
    }
}