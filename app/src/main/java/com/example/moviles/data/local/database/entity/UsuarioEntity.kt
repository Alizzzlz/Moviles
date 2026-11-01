package com.example.moviles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombres: String,
    val apellidos : String,
    val rut: String,
    val email: String,
    val password: String,
    val cargo: String,
    val rol: String = "OPERARIO",
    val obra: String = "Sin Obra Asignada"
){
    val nombreCompleto: String
        get() = "$nombres $apellidos".trim()
}