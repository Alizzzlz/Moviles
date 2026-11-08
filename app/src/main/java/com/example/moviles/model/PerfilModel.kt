package com.example.moviles.model

data class PerfilUiState(
    val cargado: Boolean = false,
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val fotoUri: String? = null,
    val cargo: String = "Maestro Mayor",
    val obra: String = "Obra Central",
    val horasMes: String = "14.5 hrs",
    val temaOscuro: Boolean? = null
)