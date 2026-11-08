package com.example.moviles.model

data class CupoHoraExtra(
    val id: Int,
    val fecha: String,
    val horario: String,
    val horas: Int,
    val motivo: String,
    val estado: String
)

data class HoraExtraUiState(
    val horasAprobadasMes: Int = 12,
    val cuposDisponibles: List<CupoHoraExtra> = emptyList(),
    val mensaje: String? = null
)