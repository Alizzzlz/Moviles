package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.datastore.UserPreferences
import com.example.moviles.util.Validadores
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    // Estado local para el ID del usuario activo
    private val _usuarioId = MutableStateFlow<Int?>(null)

    // Escuchamos los cambios del usuario según el ID seleccionado
    @OptIn(ExperimentalCoroutinesApi::class)
    private val usuarioFlow = _usuarioId.flatMapLatest { id ->
        if (id != null) usuarioDao.obtenerUsuarioPorId(id) else flowOf(null)
    }

    // Combinamos el usuario actual con el tema oscuro
    val perfil: StateFlow<PerfilUiState> = combine(
        usuarioFlow,
        prefs.isDarkTheme
    ) { usuario, oscuro ->
        PerfilUiState(
            cargado = true,
            nombre = usuario?.nombreCompleto ?: "",
            rut = usuario?.rut ?: "",
            email = usuario?.email ?: "",
            temaOscuro = oscuro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )

    /** Carga los datos del usuario logueado usando su ID */
    fun cargarPerfil(id: Int) {
        _usuarioId.value = id
    }

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