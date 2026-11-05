package com.example.moviles.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes_horas_extras")
data class SolicitudHoraExtraEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cupoId: Int,
    val usuarioId: Int = 1,
    val trabajadorNombre: String,
    val tarea: String,
    val horas: Int,
    val fecha: String,
    val estado: String = "Pendiente", // "Pendiente", "Aprobada", "Rechazada"
    val marcajeEntrada: String? = null,
    val marcajeSalida: String? = null
)