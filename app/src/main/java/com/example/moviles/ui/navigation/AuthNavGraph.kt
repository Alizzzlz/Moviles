package com.example.moviles.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviles.ui.view.auth.*

@Composable
fun AuthNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        // 1. LOGIN
        composable(route = AuthScreen.Login.route) {
            LoginView(
                onNavigateToRegister = { navController.navigate(AuthScreen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(AuthScreen.ForgotPasswordEmail.route) }
            )
        }

        // 2. REGISTRO
        composable(route = AuthScreen.Register.route) {
            RegisterView(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // 3. RECUPERAR POR CORREO
        composable(route = AuthScreen.ForgotPasswordEmail.route) {
            ForgotPasswordEmailView( // Ajustado a tu archivo ForgotPasswordEmailView.kt
                onNavigateToPhone = { navController.navigate(AuthScreen.ForgotPasswordPhone.route) },
                onNavigateToLogin = { navController.popBackStack() },
                onSendCode = { navController.navigate(AuthScreen.VerifyCode.route) }
            )
        }

        // 4. RECUPERAR POR TELÉFONO
        composable(route = AuthScreen.ForgotPasswordPhone.route) {
            ForgotPasswordPhoneView(
                onNavigateToEmail = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onSendCode = { navController.navigate(AuthScreen.VerifyCode.route) }
            )
        }

        // 5. VERIFICAR CÓDIGO
        composable(route = AuthScreen.VerifyCode.route) {
            VerifyCodeView(
                onNavigateToResetPassword = { navController.navigate(AuthScreen.ResetPassword.route) },
                onNavigateToLogin = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 6. RESTABLECER CONTRASEÑA
        composable(route = AuthScreen.ResetPassword.route) {
            ResetPasswordView(
                onPasswordResetSuccess = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}