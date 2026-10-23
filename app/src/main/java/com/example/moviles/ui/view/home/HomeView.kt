package com.example.moviles.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.ui.theme.Purple40
import com.example.moviles.ui.theme.Purple80
import com.example.moviles.ui.theme.PurpleGrey40
import com.example.moviles.ui.theme.PurpleGrey80
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelo de datos para un registro de asistencia
data class Registro(
    val fecha: String,
    val tipo: String,
    val hora: String
)

// Modelo para cada item de la barra inferior
data class NavItem(
    val titulo: String,
    val icono: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeView(nombre: String = "Juan Pérez") {
    var itemSeleccionado by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        NavItem("Inicio", Icons.Default.Home),
        NavItem("Buscar", Icons.Default.Search),
        NavItem("Avisos", Icons.Default.Notifications)
    )

    Scaffold(
        containerColor = PurpleGrey40,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Obra Check",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PurpleGrey40
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = itemSeleccionado == index,
                        onClick = { itemSeleccionado = index },
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Purple40,
                            selectedTextColor = Purple40,
                            indicatorColor = Purple80,
                            unselectedIconColor = PurpleGrey40,
                            unselectedTextColor = PurpleGrey40
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        when (itemSeleccionado) {
            0 -> HomeContent(nombre, Modifier.padding(paddingValues))
            1 -> PantallaTemporal("Búsqueda", Modifier.padding(paddingValues))
            2 -> PantallaTemporal("Notificaciones", Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun HomeContent(nombre: String, modifier: Modifier = Modifier) {
    var entradaRegistrada by remember { mutableStateOf(false) }
    var horaEntrada by remember { mutableStateOf("--:--") }
    var horaSalida by remember { mutableStateOf("--:--") }

    val historial = remember {
        listOf(
            Registro("Ayer", "Entrada", "08:02"),
            Registro("Ayer", "Salida", "17:35"),
            Registro("Lun 20", "Entrada", "07:58"),
            Registro("Lun 20", "Salida", "17:30")
        )
    }

    fun horaActual(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleGrey40),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saludo
        item {
            Column {
                Text(
                    text = "Bienvenido,",
                    fontSize = 18.sp,
                    color = PurpleGrey80
                )
                Text(
                    text = nombre,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Tarjeta de estado de hoy con botones
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Asistencia de hoy",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleGrey40
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Entrada", color = PurpleGrey40, fontSize = 14.sp)
                            Text(
                                horaEntrada,
                                color = Purple40,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Salida", color = PurpleGrey40, fontSize = 14.sp)
                            Text(
                                horaSalida,
                                color = Purple40,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                entradaRegistrada = true
                                horaEntrada = horaActual()
                            },
                            enabled = !entradaRegistrada,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Entrada")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { horaSalida = horaActual() },
                            enabled = entradaRegistrada,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salida")
                        }
                    }
                }
            }
        }

        // Resumen
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaResumen("Días asistidos", "18", Modifier.weight(1f))
                TarjetaResumen("Horas del mes", "152", Modifier.weight(1f))
            }
        }

        // Título del historial
        item {
            Text(
                text = "Registros recientes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Lista de registros
        items(historial) { registro ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleGrey80)
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
                        tint = Purple40
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            registro.tipo,
                            fontWeight = FontWeight.Bold,
                            color = PurpleGrey40
                        )
                        Text(registro.fecha, fontSize = 13.sp, color = PurpleGrey40)
                    }
                    Text(
                        registro.hora,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Purple40
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaResumen(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Purple80)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                valor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Purple40
            )
            Text(titulo, fontSize = 14.sp, color = PurpleGrey40)
        }
    }
}

@Composable
fun PantallaTemporal(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleGrey40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(texto, color = Color.White, fontSize = 22.sp)
    }
}