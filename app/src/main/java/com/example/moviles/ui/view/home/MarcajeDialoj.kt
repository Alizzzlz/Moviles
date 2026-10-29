package com.example.moviles.ui.view.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.ui.viewmodel.MarcajeDraft
import com.example.moviles.util.CameraHelper
import com.example.moviles.util.LocationHelper
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.util.Locale

/**
 * Diálogo de confirmación de marcaje. Usa 3 recursos nativos del teléfono:
 *  1) GPS      -> obtiene la ubicación (pide permiso en tiempo de ejecución)
 *  2) Cámara   -> toma una foto de respaldo
 *  3) Vibración-> feedback háptico al confirmar
 */
@Composable
fun MarcajeDialog(
    draft: MarcajeDraft,
    onUbicacionObtenida: (Double, Double) -> Unit,
    onUbicacionError: (String) -> Unit,
    onReintentarUbicacion: () -> Unit,
    onFotoTomada: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // rememberSaveable: sobrevive a rotaciones mientras la cámara está abierta
    var rutaFotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    // Cada vez que sube este contador, se lanza una búsqueda de ubicación
    var busquedas by remember { mutableIntStateOf(0) }

    // ---------- GPS ----------
    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) busquedas++
        else onUbicacionError("Permiso de ubicación denegado. Puedes confirmar sin ubicación.")
    }

    fun pedirUbicacion() {
        if (LocationHelper.tienePermiso(context)) {
            busquedas++
        } else {
            permisosLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // Al abrir el diálogo, empezamos a buscar la ubicación
    LaunchedEffect(Unit) {
        if (draft.cargandoUbicacion) pedirUbicacion()
    }

    // Cada vez que cambia "busquedas" se consulta al GPS (máximo 12 segundos)
    LaunchedEffect(busquedas) {
        if (busquedas > 0) {
            val ubicacion = withTimeoutOrNull(12_000) { LocationHelper.obtenerUbicacionActual(context) }
            if (ubicacion != null) onUbicacionObtenida(ubicacion.first, ubicacion.second)
            else onUbicacionError("No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo.")
        }
    }

    // ---------- Cámara ----------
    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val ruta = rutaFotoPendiente
        if (exito && ruta != null) onFotoTomada(ruta)
        else ruta?.let { File(it).delete() } // el usuario canceló: borramos el archivo vacío
        rutaFotoPendiente = null
    }

    fun tomarFoto() {
        val archivo = CameraHelper.crearArchivoFoto(context)
        rutaFotoPendiente = archivo.absolutePath
        try {
            camaraLauncher.launch(CameraHelper.uriDeArchivo(context, archivo))
        } catch (e: ActivityNotFoundException) {
            rutaFotoPendiente = null
            Toast.makeText(context, "Este dispositivo no tiene app de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Interfaz ----------
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Confirmar ${draft.tipo.lowercase()}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Ubicación ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        when {
                            draft.cargandoUbicacion -> Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Obteniendo ubicación...", fontSize = 13.sp)
                            }
                            draft.latitud != null && draft.longitud != null -> Text(
                                text = String.format(Locale.US, "%.5f, %.5f", draft.latitud, draft.longitud),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            else -> Text(
                                text = draft.ubicacionError ?: "Ubicación no disponible",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    if (!draft.cargandoUbicacion && draft.latitud == null) {
                        TextButton(onClick = {
                            onReintentarUbicacion()
                            pedirUbicacion()
                        }) { Text("Reintentar") }
                    }
                }

                // --- Foto ---
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Foto de respaldo (opcional)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    AnimatedVisibility(visible = draft.fotoPath != null) {
                        draft.fotoPath?.let { ruta ->
                            FotoMiniatura(
                                ruta = ruta,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                anchoMax = 800
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { tomarFoto() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (draft.fotoPath == null) "Tomar foto" else "Repetir foto")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) // vibración
                    onConfirmar()
                },
                enabled = !draft.cargandoUbicacion
            ) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
