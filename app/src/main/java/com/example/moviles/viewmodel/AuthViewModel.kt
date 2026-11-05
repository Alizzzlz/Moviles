package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.datastore.UserPreferences
import com.example.moviles.data.local.entity.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val rut: String = "",
    val isRutValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordErrorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Precargar cuenta de Administrador de prueba si no existe
        crearUsuarioAdminSiNoExiste()
    }

    private fun crearUsuarioAdminSiNoExiste() {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(getApplication()).usuarioDao()
            val adminExistente = dao.obtenerUsuarioPorEmail("admin@mark.cl")
            if (adminExistente == null) {
                dao.insertarUsuario(
                    UsuarioEntity(
                        nombres = "Jefe",
                        apellidos = "de Obra",
                        rut = "11.111.111-1",
                        email = "admin@mark.cl",
                        password = "admin123",
                        cargo = "Administrador General",
                        rol = "ADMIN",
                        obra = "Costanera Edificio B"
                    )
                )
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        val isValidFormat = newEmail.contains("@") && newEmail.contains(".")
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            isEmailValid = isValidFormat || newEmail.isEmpty(),
            errorMessage = null
        )
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            isPasswordValid = newPassword.isNotEmpty(),
            passwordErrorMessage = null
        )
    }

    fun onRutChanged(newRut: String) {
        _uiState.value = _uiState.value.copy(
            rut = newRut,
            errorMessage = null
        )
    }

    fun login(onSuccess: (Int, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val emailActual = _uiState.value.email.trim()
            val passwordActual = _uiState.value.password.trim()

            val esEmailValido = emailActual.isNotBlank() && emailActual.contains("@") && emailActual.contains(".")
            val esPasswordValida = passwordActual.isNotBlank()

            if (!esEmailValido || !esPasswordValida) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailValid = esEmailValido,
                        isPasswordValid = esPasswordValida,
                        errorMessage = if (emailActual.isEmpty()) "El correo electrónico es obligatorio." else if (!esEmailValido) "Por favor, ingresa un correo electrónico válido." else null,
                        passwordErrorMessage = if (passwordActual.isEmpty()) "La contraseña es requerida." else null
                    )
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            val dao = AppDatabase.getDatabase(getApplication()).usuarioDao()
            val usuarioEncontrado = dao.obtenerUsuarioPorEmail(emailActual)

            val esCredencialCorrecta = usuarioEncontrado != null &&
                    usuarioEncontrado.email.equals(emailActual, ignoreCase = true) &&
                    usuarioEncontrado.password == passwordActual

            if (esCredencialCorrecta && usuarioEncontrado != null) {
                // PERSISTENCIA: Guardar sesión activa en DataStore
                userPreferences.saveSession(
                    id = usuarioEncontrado.id,
                    rol = usuarioEncontrado.rol,
                    email = usuarioEncontrado.email,
                    name = usuarioEncontrado.nombres,
                    rut = usuarioEncontrado.rut
                )
                userPreferences.saveAuthToken("token_demo_123", usuarioEncontrado.rut)
                // Recordar la sesión: la próxima vez la app abre directo en Home
                userPreferences.saveSession(usuarioEncontrado.id, usuarioEncontrado.rol)

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        isEmailValid = true,
                        isPasswordValid = true,
                        errorMessage = null,
                        passwordErrorMessage = null
                    )
                    onSuccess(usuarioEncontrado.id, usuarioEncontrado.rol)
                }
            } else {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailValid = false,
                        isPasswordValid = false,
                        errorMessage = "Correo o contraseña incorrectos.",
                        passwordErrorMessage = "Correo o contraseña incorrectos."
                    )
                }
            }
        }
    }

    fun register(
        nombres: String,
        apellidos: String,
        rut: String,
        email: String,
        password: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val emailTrimmed = email.trim()
            val nombresTrimmed = nombres.trim()
            val apellidosTrimmed = apellidos.trim()
            val rutTrimmed = rut.trim()
            val passwordTrimmed = password.trim()

            if (nombresTrimmed.isBlank() || apellidosTrimmed.isBlank() || rutTrimmed.isBlank() || emailTrimmed.isBlank() || passwordTrimmed.isBlank()) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Todos los campos son obligatorios."
                    )
                }
                return@launch
            }

            withContext(Dispatchers.Main) { _uiState.value = _uiState.value.copy(isLoading = true) }

            val dao = AppDatabase.getDatabase(getApplication()).usuarioDao()

            val existente = dao.obtenerUsuarioPorEmail(emailTrimmed)
            if (existente != null) {
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Este correo electrónico ya está registrado."
                    )
                }
                return@launch
            }

            val nuevoUsuario = UsuarioEntity(
                id = 0,
                nombres = nombresTrimmed,
                apellidos = apellidosTrimmed,
                rut = rutTrimmed,
                email = emailTrimmed,
                password = passwordTrimmed,
                cargo = "Operador",
                rol = "OPERARIO",
                obra = "Costanera Edificio B"
            )

            dao.insertarUsuario(nuevoUsuario)
            val usuarioCreado = dao.obtenerUsuarioPorEmail(emailTrimmed)
            val usuarioIdGenerado = usuarioCreado?.id ?: 1

            // PERSISTENCIA: Guardar sesión activa en DataStore al registrarse
            userPreferences.saveSession(
                id = usuarioIdGenerado,
                rol = "OPERARIO",
                email = emailTrimmed,
                name = nombresTrimmed,
                rut = rutTrimmed
            )
            userPreferences.saveAuthToken("token_demo_123", rutTrimmed)
            userPreferences.saveSession(usuarioIdGenerado, "OPERARIO")

            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )
                onSuccess(usuarioIdGenerado)
            }
        }
    }
}