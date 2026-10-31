package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
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
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    // Combinamos el Flow de Room (para datos del usuario) con el Flow de DataStore (para tema oscuro)
    val perfil: StateFlow<PerfilUiState> = combine(
        usuarioDao.obtenerUsuarioPorId(1),
        prefs.isDarkTheme
    ) { usuario, oscuro ->
        PerfilUiState(
            cargado = true,
            nombre = usuario?.nombre ?: "",
            rut = usuario?.rut ?: "",
            email = usuario?.email ?: "",
            temaOscuro = oscuro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )

    fun guardarPerfil(nombre: String, rut: String) {
        viewModelScope.launch {
            // Actualizar también en DataStore si lo utilizas en otros componentes
            prefs.saveProfile(nombre.trim(), Validadores.formatearRut(rut))
        }
    }

    fun cambiarTemaOscuro(activado: Boolean) {
        viewModelScope.launch {
            prefs.setDarkTheme(activado)
        }
    }
}