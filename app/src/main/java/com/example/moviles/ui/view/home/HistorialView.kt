package com.example.moviles.ui.view.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviles.data.local.database.entity.AsistenciaEntity
import com.example.moviles.ui.componentes.EstadoRedBanner
import com.example.moviles.ui.componentes.MapaUbicacion
import com.example.moviles.ui.viewmodel.AsistenciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** "2026-10-29" -> "Hoy" / "Ayer" / "29/10/2026" */
fun formatearFecha(fechaIso: String): String {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val hoy = formato.format(Date())
    val ayer = formato.format(Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000))
    return when (fechaIso) {
        hoy -> "Hoy"
        ayer -> "Ayer"
        else -> try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(formato.parse(fechaIso)!!)
        } catch (e: Exception) {
            fechaIso
        }
    }
}

/** Una fila del historial: tipo, fecha, hora, y (si existen) foto y ubicación. */
@Composable
fun ItemMarcajeCard(registro: AsistenciaEntity) {
    val context = LocalContext.current
    var mostrarMapa by remember { mutableStateOf(false) }

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
            if (registro.fotoPath != null) {
                FotoMiniatura(
                    ruta = registro.fotoPath,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Icon(
                    imageVector = if (registro.tipo == "Entrada") Icons.Default.CheckCircle else Icons.Default.ExitToApp,
                    contentDescription = registro.tipo,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(registro.tipo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    formatearFecha(registro.fecha),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (registro.latitud != null && registro.longitud != null) {
                    TextButton(
                        onClick = { mostrarMapa = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver en mapa", fontSize = 12.sp)
                    }
                }
            }

            Text(
                registro.horaMarcaje.take(5),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    if (mostrarMapa && registro.latitud != null && registro.longitud != null) {
        AlertDialog(
            onDismissRequest = { mostrarMapa = false },
            title = {
                Text("${registro.tipo} - ${formatearFecha(registro.fecha)} ${registro.horaMarcaje.take(5)}",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                MapaUbicacion(
                    latitud = registro.latitud,
                    longitud = registro.longitud,
                    titulo = registro.tipo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    mostrarPuntoAzul = false
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarMapa = false }) { Text("Cerrar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Opcional: abrir también en la app Google Maps
                    val uri = Uri.parse("geo:${registro.latitud},${registro.longitud}?q=${registro.latitud},${registro.longitud}(${registro.tipo})")
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No hay app de mapas instalada", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Abrir en Maps") }
            }
        )
    }
}

@Composable
fun HistorialContent(
    historial: List<AsistenciaEntity>,
    modifier: Modifier = Modifier
) {
    if (historial.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Aún no tienes marcajes.\nVuelve a Inicio y marca tu entrada.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(32.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Historial de marcajes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(historial, key = { it.id }) { registro ->
                ItemMarcajeCard(registro)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialView(
    onBack: () -> Unit = {},
    viewModel: AsistenciaViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Marcajes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            EstadoRedBanner()
            HistorialContent(
                historial = state.historial,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
