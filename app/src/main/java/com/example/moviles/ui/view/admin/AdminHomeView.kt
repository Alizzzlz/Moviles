package com.example.moviles.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles.data.local.database.entity.CupoHoraExtraEntity
import com.example.moviles.data.local.entity.UsuarioEntity
import com.example.moviles.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeView(
    usuarioId: Int,
    adminViewModel: AdminViewModel = viewModel(),
    onCerrarSesion: () -> Unit = {}
) {
    var tabSeleccionado by remember { mutableStateOf(0) }
    var mostrarCrearCupoModal by remember { mutableStateOf(false) }

    val cuposPublicados by adminViewModel.cuposPublicados.collectAsState()
    val usuarios by adminViewModel.usuarios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Administrador", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Obra: Costanera Edificio B", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabSeleccionado == 0) {
                ExtendedFloatingActionButton(
                    onClick = { mostrarCrearCupoModal = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Publicar Cupo") }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    icon = { Icon(Icons.Default.HourglassTop, contentDescription = null) },
                    label = { Text("Cupos") }
                )
                NavigationBarItem(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    icon = { Icon(Icons.Default.Group, contentDescription = null) },
                    label = { Text("Usuarios") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (tabSeleccionado) {
                0 -> AdminCuposContent(
                    cupos = cuposPublicados,
                    onDeleteCupo = { id -> adminViewModel.eliminarCupo(id) }
                )
                1 -> AdminUsuariosContent(
                    usuarios = usuarios,
                    onDeleteUser = { id -> adminViewModel.eliminarUsuario(id) }
                )
            }

            if (mostrarCrearCupoModal) {
                CrearCupoDialog(
                    onDismiss = { mostrarCrearCupoModal = false },
                    onPublicar = { tarea, horas, cupos, fecha ->
                        adminViewModel.publicarCupo(tarea, horas, cupos, fecha)
                        mostrarCrearCupoModal = false
                    }
                )
            }
        }
    }
}

@Composable
fun AdminCuposContent(
    cupos: List<CupoHoraExtraEntity>,
    onDeleteCupo: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Cupos de Horas Extras Publicados", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (cupos.isEmpty()) {
            item {
                Text(
                    "No hay cupos publicados. Presiona 'Publicar Cupo' para crear uno.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            items(cupos, key = { it.id }) { cupo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cupo.tarea, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Fecha: ${cupo.fecha}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Horas: ${cupo.horas} hrs | Cupos: ${cupo.cuposDisponibles}/${cupo.cuposTotales}", fontSize = 13.sp)
                        }

                        IconButton(onClick = { onDeleteCupo(cupo.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Cupo",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsuariosContent(
    usuarios: List<UsuarioEntity>,
    onDeleteUser: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Gestión de Usuarios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (usuarios.isEmpty()) {
            item {
                Text("No hay usuarios registrados en el sistema.", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            items(usuarios, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(user.email, fontSize = 13.sp)
                            Text("Rol: ${user.rol} | Cargo: ${user.cargo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                        IconButton(onClick = { onDeleteUser(user.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}