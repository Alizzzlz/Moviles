package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.datastore.UserPreferences
import com.example.moviles.util.Validadores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val temaOscuro: Boolean = false
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    private val _usuarioId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val usuarioFlow = _usuarioId.flatMapLatest { id ->
        if (id != null) usuarioDao.obtenerUsuarioPorId(id) else flowOf(null)
    }

    val perfil: StateFlow<PerfilUiState> = combine(
        usuarioFlow,
        prefs.userEmail,
        prefs.userPhone,
        prefs.userImageUri,
        prefs.isDarkTheme
    ) { usuario, correoEditado, telefonoGuardado, fotoGuardada, oscuro ->
        PerfilUiState(
            cargado = true,
            nombre = usuario?.nombreCompleto ?: "",
            rut = usuario?.rut ?: "",
            email = correoEditado.ifBlank { usuario?.email ?: "" },
            telefono = telefonoGuardado,
            fotoUri = fotoGuardada,
            cargo = usuario?.cargo?.ifBlank { "Operador" } ?: "Operador",
            obra = "Obra Central",
            horasMes = "14.5 hrs",
            temaOscuro = oscuro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PerfilUiState()
    )

    fun cargarPerfil(id: Int) {
        _usuarioId.value = id
    }

    fun guardarContacto(nuevoEmail: String, nuevoTelefono: String) {
        viewModelScope.launch {
            val telefonoFormateado = Validadores.formatearTelefono(nuevoTelefono)
            prefs.saveContactInfo(nuevoEmail.trim(), telefonoFormateado)
        }
    }

    fun guardarFotoPerfil(uri: String) {
        viewModelScope.launch {
            prefs.saveProfileImageUri(uri)
        }
    }

    fun cambiarTemaOscuro(activado: Boolean) {
        viewModelScope.launch {
            prefs.setDarkTheme(activado)
        }
    }

    fun cerrarSesion(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.clearSession()
            withContext(Dispatchers.Main) {
                onLogoutSuccess()
            }
        }
    }
}