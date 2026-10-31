package com.example.moviles.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Marcaje "en borrador": se está armando en el diálogo (GPS + foto) y aún no se guarda. */
data class MarcajeDraft(
    val tipo: String,                       // "Entrada" o "Salida"
    val cargandoUbicacion: Boolean = true,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val ubicacionError: String? = null,
    val fotoPath: String? = null
)

data class AsistenciaUiState(
    val estadoMarcaje: Int = 0,            // 0 = sin marcar, 1 = marcó entrada, 2 = jornada completa
    val horaEntrada: String = "--:--",
    val horaSalida: String = "--:--",
    val historial: List<AsistenciaEntity> = emptyList(),
    val diasAsistidos: Int = 0,
    val draft: MarcajeDraft? = null,
    val mensaje: String? = null
)

class AsistenciaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).asistenciaDao()

    private val _draft = MutableStateFlow<MarcajeDraft?>(null)
    private val _mensaje = MutableStateFlow<String?>(null)

    /**
     * Combina 3 fuentes en un solo estado para la pantalla:
     * el historial de Room (se actualiza solo), el borrador del diálogo y el mensaje.
     */
    val uiState: StateFlow<AsistenciaUiState> = combine(
        dao.obtenerHistorial(), _draft, _mensaje
    ) { historial, draft, mensaje ->
        val hoy = fechaHoy()
        val deHoy = historial.filter { it.fecha == hoy }
        val entrada = deHoy.lastOrNull { it.tipo == "Entrada" }
        val salida = deHoy.lastOrNull { it.tipo == "Salida" }

        AsistenciaUiState(
            estadoMarcaje = when {
                salida != null -> 2
                entrada != null -> 1
                else -> 0
            },
            horaEntrada = entrada?.horaMarcaje?.take(5) ?: "--:--",
            horaSalida = salida?.horaMarcaje?.take(5) ?: "--:--",
            historial = historial,
            diasAsistidos = historial.filter { it.tipo == "Entrada" }.map { it.fecha }.distinct().size,
            draft = draft,
            mensaje = mensaje
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AsistenciaUiState())

    // ---------- Flujo de marcaje ----------

    fun iniciarMarcaje(tipo: String) {
        _draft.value = MarcajeDraft(tipo = tipo)
    }

    fun ubicacionObtenida(lat: Double, lng: Double) {
        _draft.value = _draft.value?.copy(
            cargandoUbicacion = false, latitud = lat, longitud = lng, ubicacionError = null
        )
    }

    fun ubicacionFallida(motivo: String) {
        _draft.value = _draft.value?.copy(cargandoUbicacion = false, ubicacionError = motivo)
    }

    fun reintentarUbicacion() {
        _draft.value = _draft.value?.copy(cargandoUbicacion = true, ubicacionError = null)
    }

    fun fotoTomada(ruta: String) {
        val anterior = _draft.value?.fotoPath
        if (anterior != null && anterior != ruta) borrarArchivo(anterior)
        _draft.value = _draft.value?.copy(fotoPath = ruta)
    }

    fun cancelarMarcaje() {
        _draft.value?.fotoPath?.let { borrarArchivo(it) }
        _draft.value = null
    }

    /** Guarda el marcaje en Room con la hora EXACTA del sistema, el GPS y la foto. */
    fun confirmarMarcaje() {
        val borrador = _draft.value ?: return
        viewModelScope.launch {
            val ahora = Date()
            dao.insertarAsistencia(
                AsistenciaEntity(
                    fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ahora),
                    horaMarcaje = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(ahora),
                    tipo = borrador.tipo,
                    sincronizado = false,
                    latitud = borrador.latitud,
                    longitud = borrador.longitud,
                    fotoPath = borrador.fotoPath
                )
            )
            _draft.value = null
            _mensaje.value = "${borrador.tipo} registrada correctamente"
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    // ---------- Utilidades ----------

    private fun fechaHoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun borrarArchivo(ruta: String) {
        viewModelScope.launch(Dispatchers.IO) { File(ruta).delete() }
    }
}





