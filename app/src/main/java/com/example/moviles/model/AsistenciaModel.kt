package com.example.moviles.model // Ajusta a la ruta real de tu paquete model

import com.example.moviles.data.local.database.entity.AsistenciaEntity

/** Marcaje "en borrador": se está armando en el diálogo (GPS + foto) y aún no se guarda. */
data class MarcajeDraft(
    val tipo: String,                       // "Entrada", "Inicio Colación", "Fin Colación" o "Salida"
    val cargandoUbicacion: Boolean = true,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val ubicacionError: String? = null,
    val fotoPath: String? = null
)

data class AsistenciaUiState(
    val estadoMarcaje: Int = 0,            // 0 = Entrada, 1 = Inicio Colación, 2 = Fin Colación, 3 = Salida, 4 = Completa
    val horaEntrada: String = "--:--",
    val horaInicioColacion: String = "--:--",
    val horaFinColacion: String = "--:--",
    val horaSalida: String = "--:--",
    val historial: List<AsistenciaEntity> = emptyList(),
    val diasAsistidos: Int = 0,
    val draft: MarcajeDraft? = null,
    val mensaje: String? = null
)