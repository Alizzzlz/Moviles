package com.example.moviles.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: Int = 1,
    val nombre: String,
    val rut: String,
    val email: String,
    val password: String,
    val cargo: String
)