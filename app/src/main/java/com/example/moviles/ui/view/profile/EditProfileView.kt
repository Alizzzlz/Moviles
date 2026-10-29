package com.example.moviles.ui.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles.ui.componentes.CampoValidado
import com.example.moviles.util.Validadores
import com.example.moviles.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileView(
    viewModel: PerfilViewModel = viewModel(),
    onVolver: () -> Unit = {}
) {
    val perfil by viewModel.perfil.collectAsState()

    var nombre by rememberSaveable { mutableStateOf("") }
    var rut by rememberSaveable { mutableStateOf("") }
    var iniciado by rememberSaveable { mutableStateOf(false) }
    var intento by rememberSaveable { mutableStateOf(false) }

    // Cuando DataStore termina de leer, rellenamos el formulario UNA sola vez
    LaunchedEffect(perfil.cargado) {
        if (perfil.cargado && !iniciado) {
            nombre = perfil.nombre
            rut = perfil.rut
            iniciado = true
        }
    }

    val errorNombre = if (nombre.isNotEmpty() || intento) Validadores.nombreError(nombre) else null
    val errorRut = if (rut.isNotEmpty() || intento) Validadores.rutError(rut) else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoValidado(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre completo",
                error = errorNombre
            )
            CampoValidado(
                value = rut,
                onValueChange = { rut = it },
                label = "RUT",
                placeholder = "12.345.678-5",
                imeAction = ImeAction.Done,
                error = errorRut
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    intento = true
                    if (Validadores.nombreError(nombre) == null && Validadores.rutError(rut) == null) {
                        viewModel.guardarPerfil(nombre, rut)
                        onVolver()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}
