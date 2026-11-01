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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var email by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var iniciado by rememberSaveable { mutableStateOf(false) }
    var intento by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(perfil.cargado) {
        if (perfil.cargado && !iniciado) {
            email = perfil.email
            telefono = perfil.telefono
            iniciado = true
        }
    }

    val errorEmail = if (email.isNotEmpty() || intento) Validadores.emailError(email) else null
    val errorTelefono = if (telefono.isNotEmpty() || intento) Validadores.telefonoError(telefono) else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Editar datos de contacto", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // --- DATOS DE CONTACTO EDITABLES ---
            CampoValidado(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                placeholder = "ejemplo@constructora.cl",
                keyboardType = KeyboardType.Email,
                error = errorEmail
            )

            CampoValidado(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono de contacto",
                placeholder = "912345678",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
                error = errorTelefono
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    intento = true
                    if (Validadores.emailError(email) == null && Validadores.telefonoError(telefono) == null) {
                        viewModel.guardarContacto(email, telefono)
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