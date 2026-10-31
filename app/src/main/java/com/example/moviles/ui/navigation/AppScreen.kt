package com.example.moviles.ui.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")


    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPasswordEmail : Screen("forgot_password_email")
    object ForgotPasswordPhone : Screen("forgot_password_phone")
    object VerifyCode : Screen("verify_code")
    object ResetPassword : Screen("reset_password")


    object Home : Screen("home")
    object Historial : Screen("historial")
    object HoraExtra : Screen("hora_extra")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
}