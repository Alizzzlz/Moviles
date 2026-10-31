package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val rut: String = "",
    val isRutValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true, // <-- Nuevo campo de validez
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordErrorMessage: String? = null, // <-- Error específico para la contraseña
    val isAuthenticated: Boolean = false
)

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

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
            isPasswordValid = newPassword.isNotEmpty(), // Resetea el error al escribir
            passwordErrorMessage = null
        )
    }

    fun onRutChanged(newRut: String) {
        _uiState.value = _uiState.value.copy(
            rut = newRut,
            errorMessage = null
        )
    }

    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val emailActual = _uiState.value.email.trim()
            val passwordActual = _uiState.value.password.trim()

            val esEmailValido =
                emailActual.isNotBlank() && emailActual.contains("@") && emailActual.contains(".")
            val esPasswordValida = passwordActual.isNotBlank()

            // 1. Validar que los campos no estén vacíos
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

            // 2. Buscar usuario en Room
            val dao = AppDatabase.getDatabase(getApplication()).usuarioDao()
            val usuarioEncontrado = dao.obtenerUsuarioPorEmail(emailActual)

            // 3. Comparar correo y contraseña (ignorando mayúsculas/minúsculas en el email)
            val esCredencialCorrecta = usuarioEncontrado != null &&
                    usuarioEncontrado.email.equals(emailActual, ignoreCase = true) &&
                    usuarioEncontrado.password == passwordActual

            if (esCredencialCorrecta && usuarioEncontrado != null) {
                // Guardar token en DataStore
                userPreferences.saveAuthToken("token_demo_123", usuarioEncontrado.rut)

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        isEmailValid = true,
                        isPasswordValid = true,
                        errorMessage = null,
                        passwordErrorMessage = null
                    )
                    // Retornar nombre a la vista de navegación
                    onSuccess(usuarioEncontrado.nombre)
                }
            } else {
                // Rechazar login si la contraseña o correo no coinciden
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
}