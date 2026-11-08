package com.example.moviles.model

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