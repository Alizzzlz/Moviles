package com.example.moviles.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
                onLoginSuccess = { usuarioId ->
                    navController.navigate(Screen.Home.createRoute(usuarioId)) {
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
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { usuarioId ->
                    navController.navigate(Screen.Home.createRoute(usuarioId)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
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

        // --- HOME (Acepta el ID del usuario como argumento de ruta) ---
        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable

            HomeView(
                usuarioId = usuarioId,
                onNavegarAHorasExtras = { navController.navigate(Screen.HoraExtra.route) },
                onNavegarANotificaciones = { navController.navigate(Screen.Notifications.route) },
                onNavegarAEditarPerfil = { navController.navigate(Screen.EditProfile.route) },
                onNavegarAAjustes = { navController.navigate(Screen.Settings.route) },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
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
                    nombre = perfilState.nombre.ifBlank { "Usuario" },
                    rut = perfilState.rut,
                    email = perfilState.email,
                    telefono = perfilState.telefono,
                    fotoUri = perfilState.fotoUri,
                    cargo = perfilState.cargo,
                    obra = perfilState.obra,
                    horasMes = perfilState.horasMes,
                    onSeleccionarFoto = { uri ->
                        perfilViewModel.guardarFotoPerfil(uri.toString())
                    },
                    onEditarPerfil = { navController.navigate(Screen.EditProfile.route) },
                    onAjustes = { navController.navigate(Screen.Settings.route) },
                    onCerrarSesion = {
                        perfilViewModel.cerrarSesion {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(Screen.EditProfile.route) { backStackEntry ->
            // Buscamos la entrada de la pantalla Profile de forma segura
            val profileEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Screen.Profile.route)
                } catch (e: Exception) {
                    null
                }
            }

            // Si encontramos la ruta anterior usamos su ViewModel, si no, creamos uno nuevo asociado a EditProfile
            val perfilViewModel: PerfilViewModel = if (profileEntry != null) {
                viewModel(profileEntry)
            } else {
                viewModel()
            }

            EditProfileView(
                viewModel = perfilViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsView(onVolver = { navController.popBackStack() })
        }

        // --- AUTH ---
        composable(Screen.Login.route) {
            LoginView(
                onLoginSuccess = { usuarioId ->
                    navController.navigate(Screen.Home.createRoute(usuarioId)) {
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
                onNavigateToLogin = {
                    navController.popBackStack() // Mantiene el comportamiento original de regresar al Login
                },
                onNavigateToHome = { usuarioId ->
                    navController.navigate(Screen.Home.createRoute(usuarioId)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
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
    }
}