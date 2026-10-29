package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.datastore.UserPreferences
import com.example.moviles.util.Validadores
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PerfilUiState(
    val cargado: Boolean = false,
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val temaOscuro: Boolean = false
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)

    val perfil: StateFlow<PerfilUiState> = combine(
        prefs.userName,
        prefs.userRut,
        prefs.userEmail,
        prefs.isDarkTheme
    ) { nombre, rut, email, oscuro ->
        PerfilUiState(
            cargado = true,
            nombre = nombre,
            rut = rut,
            email = email,
            temaOscuro = oscuro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState() // <-- Los paréntesis () resuelven el error principal
    )

    fun guardarPerfil(nombre: String, rut: String) {
        viewModelScope.launch {
            prefs.saveProfile(nombre.trim(), Validadores.formatearRut(rut))
        }
    }

    fun cambiarTemaOscuro(activado: Boolean) {
        viewModelScope.launch {
            prefs.setDarkTheme(activado)
        }
    }
}
