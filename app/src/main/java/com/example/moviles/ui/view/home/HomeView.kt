package com.example.moviles.ui.view.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles.ui.theme.MarkTheme
import com.example.moviles.ui.view.profile.ProfileView
import com.example.moviles.ui.viewmodel.AsistenciaUiState
import com.example.moviles.ui.viewmodel.AsistenciaViewModel
import com.example.moviles.viewmodel.PerfilViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NavItem(val titulo: String, val icono: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    usuarioId: Int,
    nombreUsuario: String = "Usuario",
    viewModel: AsistenciaViewModel = viewModel(),
    perfilViewModel: PerfilViewModel = viewModel(),
    onNavegarAHorasExtras: () -> Unit = {},
    onNavegarANotificaciones: () -> Unit = {},
    onNavegarAEditarPerfil: () -> Unit = {},
    onNavegarAAjustes: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    LaunchedEffect(usuarioId) {
        perfilViewModel.cargarPerfil(usuarioId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val perfil by perfilViewModel.perfil.collectAsStateWithLifecycle()

    var itemSeleccionado by rememberSaveable { mutableStateOf(0) }
    var mostrarAvisosDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home),
        NavItem("Historial", Icons.Default.DateRange),
        NavItem("Mi Perfil", Icons.Default.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = "Logo MARK",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MARK",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            letterSpacing = 1.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarAvisosDialog = true }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Avisos")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = itemSeleccionado == index,
                        onClick = { itemSeleccionado = index },
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Crossfade(
            targetState = itemSeleccionado,
            animationSpec = tween(300),
            modifier = Modifier.padding(paddingValues),
            label = "pestañas"
        ) { pestana ->
            when (pestana) {
                0 -> HomeContent(
                    nombre = perfil.nombre.ifBlank { nombreUsuario },
                    state = state,
                    onIniciarMarcaje = viewModel::iniciarMarcaje,
                    onNavegarAHorasExtras = onNavegarAHorasExtras
                )
                1 -> HistorialContent(historial = state.historial)
                else -> ProfileView(
                    nombre = perfil.nombre.ifBlank { nombreUsuario },
                    rut = perfil.rut,
                    email = perfil.email,
                    onEditarPerfil = onNavegarAEditarPerfil,
                    onAjustes = onNavegarAAjustes,
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    }

    if (mostrarAvisosDialog) {
        AlertDialog(
            onDismissRequest = { mostrarAvisosDialog = false },
            title = { Text("Avisos y Aprobaciones") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Hora Extra Aprobada: Sábado 31 (3 hrs).", fontSize = 14.sp)
                    Text("• Postulación Pendiente: Refuerzo Hormigón.", fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarAvisosDialog = false }) { Text("Entendido") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarAvisosDialog = false
                    onNavegarANotificaciones()
                }) { Text("Ver todas") }
            }
        )
    }

    state.draft?.let { borrador ->
        MarcajeDialog(
            draft = borrador,
            onUbicacionObtenida = viewModel::ubicacionObtenida,
            onUbicacionError = viewModel::ubicacionFallida,
            onReintentarUbicacion = viewModel::reintentarUbicacion,
            onFotoTomada = viewModel::fotoTomada,
            onConfirmar = viewModel::confirmarMarcaje,
            onCancelar = viewModel::cancelarMarcaje
        )
    }
}

@Composable
fun HomeContent(
    nombre: String,
    state: AsistenciaUiState,
    onIniciarMarcaje: (String) -> Unit,
    onNavegarAHorasExtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ESTADO PARA LA HORA Y FECHA DINÁMICA ---
    var fechaActual by remember { mutableStateOf("") }
    var horaActual by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val formatoFecha = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "CL"))
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            val now = Date()
            fechaActual = formatoFecha.format(now).replaceFirstChar { it.uppercase() }
            horaActual = formatoHora.format(now)
            delay(1000L) // Actualiza el reloj cada segundo
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saludo con Avatar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombre.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "¡Hola, $nombre!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Obra: Costanera Edificio B",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        // --- TARJETA DE ASISTENCIA CON DÍA/HORA EN VIVO Y MARCAJE ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Encabezado de la tarjeta con la fecha actual
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Asistencia de hoy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = fechaActual.ifBlank { "Cargando fecha..." },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Muestra la Hora Servidor / Hora en vivo
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = horaActual.ifBlank { "--:--:--" },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Horas registradas de Entrada y Salida
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Entrada", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                            Text(
                                text = state.horaEntrada,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Salida", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                            Text(
                                text = state.horaSalida,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = state.estadoMarcaje,
                        transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(200)) },
                        label = "boton_marcaje"
                    ) { estado ->
                        when (estado) {
                            0 -> Button(
                                onClick = { onIniciarMarcaje("Entrada") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MARCAR ENTRADA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            1 -> Button(
                                onClick = { onIniciarMarcaje("Salida") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MARCAR SALIDA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            else -> OutlinedButton(
                                onClick = { },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.TaskAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("JORNADA REGISTRADA", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- NUEVO APARTADO DIRECTO PARA HORAS EXTRAS ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarAHorasExtras() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreTime,
                                contentDescription = "Horas Extras",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gestión de Horas Extras",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Postula y revisa tus cupos aprobados",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ir a Horas Extras",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Tarjetas de Resumen Adicional
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${state.diasAsistidos}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Días asistidos", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("12 hrs", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Horas Aprobadas", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }

        // Últimos Marcajes
        item {
            Text(
                text = "Últimos Marcajes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (state.historial.isEmpty()) {
            item {
                Text(
                    "Todavía no hay marcajes. ¡Marca tu entrada!",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp
                )
            }
        } else {
            items(state.historial.take(5).size) { indice ->
                ItemMarcajeCard(state.historial[indice])
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    MarkTheme {
        HomeContent(
            nombre = "Juan Pérez",
            state = AsistenciaUiState(),
            onIniciarMarcaje = {},
            onNavegarAHorasExtras = {}
        )
    }
}