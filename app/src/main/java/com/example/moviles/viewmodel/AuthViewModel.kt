package com.example.moviles.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class AuthUiState(
    val email: String = "",
    val password: String = "", // Agregado para el login
    val rut: String = "",
    val isRutValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(application: Application): AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        val isValid = newEmail.contains("@") && newEmail.contains(".")
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            isEmailValid = isValid || newEmail.isEmpty(),
            errorMessage = null
        )
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            errorMessage = null
        )
    }

    fun onRutChanged(newRut: String) {
        _uiState.value = _uiState.value.copy(
            rut = newRut,
            errorMessage = null
        )
    }

    // Cambiamos onSuccess: () -> Unit por onSuccess: (String) -> Unit
    fun login(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val emailActual = _uiState.value.email

            if (emailActual.isNotBlank() && _uiState.value.isEmailValid) {
                // Extrae un nombre de usuario formateado (Ej: "juan.perez@obra.cl" -> "Juan.perez")
                val nombreUsuario = emailActual.substringBefore("@")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                userPreferences.saveAuthToken("token_demo_123", _uiState.value.rut)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )

                onSuccess(nombreUsuario)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Por favor, ingresa un correo electrónico válido."
                )
            }
        }
    }
}