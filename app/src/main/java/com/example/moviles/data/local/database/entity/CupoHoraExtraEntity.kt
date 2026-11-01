package com.example.moviles.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cupos_horas_extras")
data class CupoHoraExtraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tarea: String,
    val horas: Int,
    val cuposTotales: Int,
    val cuposDisponibles: Int,
    val fecha: String,
    val obra: String
)