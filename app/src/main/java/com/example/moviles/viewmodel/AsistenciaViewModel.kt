package com.example.moviles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.database.AppDatabase
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AsistenciaViewModel(application: Application) : AndroidViewModel(application) {

    private val asistenciaDao = AppDatabase.getDatabase(application).asistenciaDao()

    // Flujo reactivo del historial guardado en Room
    val historialAsistencias: StateFlow<List<AsistenciaEntity>> = asistenciaDao.obtenerHistorial()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registrarMarcajeAutomático(tipoMarcaje: String) {
        viewModelScope.launch {
            // Capturamos la hora y fecha EXACTA del sistema en el momento del clic
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val nuevaAsistencia = AsistenciaEntity(
                fecha = fechaActual,
                horaMarcaje = horaActual,
                tipo = tipoMarcaje, // "Entrada" o "Salida"
                sincronizado = false
            )

            asistenciaDao.insertarAsistencia(nuevaAsistencia)
        }
    }
}