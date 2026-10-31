package com.example.moviles.ui.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")

    // --- AUTENTICACIÓN ---
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPasswordEmail : Screen("forgot_password_email")
    object ForgotPasswordPhone : Screen("forgot_password_phone")
    object VerifyCode : Screen("verify_code")
    object ResetPassword : Screen("reset_password")

    // --- PANTALLAS PRINCIPALES (Rutas Dinámicas) ---
    object Home : Screen("home/{usuarioId}") {
        fun createRoute(usuarioId: Int) = "home/$usuarioId"
    }

    object Profile : Screen("profile/{usuarioId}") {
        fun createRoute(usuarioId: Int) = "profile/$usuarioId"
    }

    // --- OTRAS PANTALLAS ---
    object Historial : Screen("historial")
    object HoraExtra : Screen("hora_extra")
    object Notifications : Screen("notifications")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
}