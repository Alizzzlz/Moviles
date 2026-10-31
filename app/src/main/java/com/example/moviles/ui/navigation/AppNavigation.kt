package com.example.moviles.ui.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moviles.ui.view.auth.*
import com.example.moviles.ui.view.home.*
import com.example.moviles.ui.view.onboarding.*
import com.example.moviles.ui.view.profile.*
import com.example.moviles.ui.view.settings.*
import com.example.moviles.ui.view.splash.*
import com.example.moviles.viewmodel.PerfilViewModel

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- SPLASH ---
        composable(Screen.Splash.route) {
            SplashView(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // --- AUTH ---
        composable(Screen.Login.route) {
            LoginView(
                onLoginSuccess = { _ ->
                    // Navegación directa al Home de forma limpia
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPasswordEmail.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterView(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPasswordEmail.route) {
            ForgotPasswordEmailView(
                onNavigateToPhone = { navController.navigate(Screen.ForgotPasswordPhone.route) },
                onNavigateToLogin = { navController.popBackStack() },
                onSendCode = { navController.navigate(Screen.VerifyCode.route) }
            )
        }

        composable(Screen.ForgotPasswordPhone.route) {
            ForgotPasswordPhoneView(
                onNavigateToEmail = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSendCode = { navController.navigate(Screen.VerifyCode.route) }
            )
        }

        composable(Screen.VerifyCode.route) {
            VerifyCodeView(
                onNavigateToResetPassword = { navController.navigate(Screen.ResetPassword.route) },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordView(
                onPasswordResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- HOME ---
        composable(Screen.Home.route) {
            HomeView()
        }

        composable(Screen.HoraExtra.route) {
            HoraExtraView(
                onVolverAHome = { navController.popBackStack() }
            )
        }

        composable(Screen.Historial.route) {
            HistorialView(onBack = { navController.popBackStack() })
        }

        composable(Screen.Notifications.route) {
            NotificationListView(
                onVolverAHome = { navController.popBackStack() }
            )
        }

        // --- PERFIL ---
        composable(Screen.Profile.route) {
            val perfilViewModel: PerfilViewModel = viewModel()
            val perfilState by perfilViewModel.perfil.collectAsStateWithLifecycle()

            if (perfilState.cargado) {
                ProfileView(
                    nombre = if (perfilState.nombre.isNotBlank()) perfilState.nombre else "Usuario",
                    rut = perfilState.rut,
                    email = perfilState.email,
                    onEditarPerfil = { navController.navigate(Screen.EditProfile.route) },
                    onAjustes = { navController.navigate(Screen.Settings.route) },
                    onCerrarSesion = {
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                )
            } else {
                CircularProgressIndicator()
            }
        }

        composable(Screen.EditProfile.route) {
            EditProfileView(onVolver = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsView(onVolver = { navController.popBackStack() })
        }
    }
}