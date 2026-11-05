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
import com.example.moviles.ui.view.admin.*
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
            val context = androidx.compose.ui.platform.LocalContext.current
            val userPreferences = remember { com.example.moviles.data.local.datastore.UserPreferences(context) }
            val isLoggedIn by userPreferences.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
            val userRole by userPreferences.userRole.collectAsStateWithLifecycle(initialValue = "")
            val userId by userPreferences.userId.collectAsStateWithLifecycle(initialValue = -1)

            SplashView(
                onFinished = {
                    val destination = if (isLoggedIn && userId != -1) {
                        if (userRole == "ADMIN") {
                            Screen.AdminHome.createRoute(userId)
                        } else {
                            Screen.Home.createRoute(userId)
                        }
                    } else {
                        Screen.Login.route
                    }

                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // --- AUTH ---
        composable(Screen.Login.route) {
            LoginView(
                onLoginSuccessWithRole = { usuarioId, rol ->
                    val destination = if (rol == "ADMIN") {
                        Screen.AdminHome.createRoute(usuarioId)
                    } else {
                        Screen.Home.createRoute(usuarioId)
                    }
                    navController.navigate(destination) {
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
                    navController.popBackStack()
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

        // --- HOME (OPERARIO) ---
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

        // --- HOME (ADMINISTRADOR / JEFE DE OBRA) ---
        composable(
            route = Screen.AdminHome.route,
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable

            AdminHomeView(
                usuarioId = usuarioId,
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HoraExtra.route) {
            HoraExtraView()
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
            val profileEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Screen.Profile.route)
                } catch (e: Exception) {
                    null
                }
            }

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
    }
}