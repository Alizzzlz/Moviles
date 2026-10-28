package com.example.moviles.ui.view.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.ui.theme.ObraCheckTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Registro(val fecha: String, val tipo: String, val hora: String)
data class NavItem(val titulo: String, val icono: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    nombreUsuario: String = "Juan Pérez",
    onNavegarAHorasExtras: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var itemSeleccionado by remember { mutableIntStateOf(0) }
    var mostrarAvisosDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home),
        NavItem("Historial", Icons.Default.DateRange),
        NavItem("Mi Perfil", Icons.Default.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Badge con la inicial o Logo de MARK
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
                        BadgedBox(
                            badge = { Badge { Text("2") } }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Avisos"
                            )
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
        when (itemSeleccionado) {
            0 -> HomeContent(
                nombre = nombreUsuario,
                onNavegarAHorasExtras = onNavegarAHorasExtras,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Sección Historial")
            }
            2 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Sección Perfil")
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
                    TextButton(onClick = { mostrarAvisosDialog = false }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

@Composable
fun HomeContent(
    nombre: String,
    onNavegarAHorasExtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado del marcaje: 0 = No ha marcado, 1 = Marcó entrada, 2 = Marcó salida
    var estadoMarcaje by remember { mutableIntStateOf(0) }
    var horaEntrada by remember { mutableStateOf("--:--") }
    var horaSalida by remember { mutableStateOf("--:--") }

    val historial = remember {
        listOf(
            Registro("Ayer", "Entrada", "08:02"),
            Registro("Ayer", "Salida", "17:35"),
            Registro("Lun 20", "Entrada", "07:58")
        )
    }

    fun horaActual(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

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

        // Tarjeta de Asistencia con Botón Único Dinámico (Ultra Amigable)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Asistencia de hoy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hora Servidor",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Entrada", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                            Text(
                                horaEntrada,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Salida", color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                            Text(
                                horaSalida,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Inteligente de 1 Solo Toque
                    when (estadoMarcaje) {
                        0 -> { // Registrar Entrada
                            Button(
                                onClick = {
                                    horaEntrada = horaActual()
                                    estadoMarcaje = 1
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MARCAR ENTRADA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        1 -> { // Registrar Salida
                            Button(
                                onClick = {
                                    horaSalida = horaActual()
                                    estadoMarcaje = 2
                                },
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
                        }
                        else -> { // Jornada Completa
                            OutlinedButton(
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

        // Tarjetas de Resumen
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
                        Text("18", fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavegarAHorasExtras,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver Cupos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
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

        items(historial) { registro ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (registro.tipo == "Entrada")
                            Icons.Default.CheckCircle else Icons.Default.ExitToApp,
                        contentDescription = registro.tipo,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            registro.tipo,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            registro.fecha,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(
                        registro.hora,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeViewPreview() {
    ObraCheckTheme {
        HomeView(nombreUsuario = "Juan Pérez")
    }
}