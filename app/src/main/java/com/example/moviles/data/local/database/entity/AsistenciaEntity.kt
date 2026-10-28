package com.example.moviles.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,        // Ejemplo: "2026-10-28"
    val horaMarcaje: String,   // Capturada automáticamente al marcar (Ejemplo: "17:45:00")
    val tipo: String,         // "Entrada" o "Salida"
    val sincronizado: Boolean = false
)