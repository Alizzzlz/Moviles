package com.example.moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.moviles.ui.navigation.MainNavGraph
import com.example.moviles.ui.theme.MarkTheme
import com.example.moviles.viewmodel.PerfilViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val perfilViewModel: PerfilViewModel = viewModel()
            val perfilState by perfilViewModel.perfil.collectAsStateWithLifecycle()

            val temaDelSistema = isSystemInDarkTheme()

            // Si perfilState.temaOscuro es true/false se usa ese valor;
            // si es null (no se ha configurado) o el perfil no ha cargado, usa temaDelSistema.
            val esTemaOscuro = perfilState.temaOscuro ?: temaDelSistema

            MarkTheme(darkTheme = esTemaOscuro) {
                val navController = rememberNavController()
                MainNavGraph(navController = navController)
            }
        }
    }
}