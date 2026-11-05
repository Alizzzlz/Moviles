package com.example.moviles.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.moviles.data.local.AppDatabase
import com.example.moviles.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context.applicationContext) }

    /** Borra la sesión guardada y vuelve al Login (limpiando todo el historial de pantallas). */
    val cerrarSesion: () -> Unit = {
        scope.launch {
            userPrefs.clearSession()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- SPLASH ---
        composable(Screen.Splash.route) {
            SplashView(
                onFinished = {
                    scope.launch {
                        // ¿Hay una sesión guardada? Entonces se salta el Login.
                        val sesion = userPrefs.getSession()
                        val usuario = sesion?.let {
                            AppDatabase.getDatabase(context).usuarioDao()
                                .obtenerUsuarioPorId(it.first).first()
                        }

                        val destino = when {
                            // sesión inexistente o el usuario ya no existe en la base de datos
                            sesion == null || usuario == null -> {
                                if (sesion != null) userPrefs.clearSession()
                                Screen.Login.route
                            }
                            usuario.rol == "ADMIN" -> Screen.AdminHome.createRoute(usuario.id)
                            else -> Screen.Home.createRoute(usuario.id)
                        }

                        navController.navigate(destino) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
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
                onCerrarSesion = cerrarSesion
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
                onCerrarSesion = cerrarSesion
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