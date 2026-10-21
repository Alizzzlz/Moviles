package com.example.moviles.ui.navigation

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
    object ForgotPasswordEmail : AuthScreen("forgot_password_email")
    object ForgotPasswordPhone : AuthScreen("forgot_password_phone")
    object VerifyCode : AuthScreen("verify_code")
    object ResetPassword : AuthScreen("reset_password")
}