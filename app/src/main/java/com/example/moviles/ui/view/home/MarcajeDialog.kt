package com.example.moviles.ui.view.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
import com.example.moviles.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.moviles.util.CameraHelper



/**
 * Data class auxiliar para representar el estado del marcaje dentro del diálogo.
 */
data class MarcajeDraft(
    val tipo: String = "Entrada",
    val cargandoUbicacion: Boolean = true,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val ubicacionError: String? = null,
    val fotoPath: String? = null
)

/**
 * Diálogo interactivo con Material 3 para registrar entrada/salida.
 * Incluye reloj en tiempo real, permisos de GPS, captura opcional con cámara y vibración háptica.
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

    // Ruta de foto que sobrevive a rotaciones de pantalla
    var rutaFotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var busquedas by remember { mutableIntStateOf(0) }

    // ---------- Reloj en vivo ----------
    val hora by produceState(initialValue = horaActual()) {
        while (true) {
            value = horaActual()
            delay(1000)
        }
    }

    // ---------- GPS y Permisos ----------
    val permisosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            busquedas++
        } else {
            onUbicacionError("Permiso de ubicación denegado. Puedes registrar sin ubicación.")
        }
    }

    fun pedirUbicacion() {
        if (LocationHelper.tienePermiso(context)) {
            busquedas++
        } else {
            permisosLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (draft.cargandoUbicacion) pedirUbicacion()
    }

    LaunchedEffect(busquedas) {
        if (busquedas > 0) {
            val ubicacion = withTimeoutOrNull(12_000L) {
                LocationHelper.obtenerUbicacion(context)
            }
            if (ubicacion != null) {
                onUbicacionObtenida(ubicacion.first, ubicacion.second)
            } else {
                onUbicacionError("No se pudo obtener la ubicación. Revisa que el GPS esté activo.")
            }
        }
    }

    // ---------- Cámara ----------
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val ruta = rutaFotoPendiente
        if (exito && ruta != null) {
            onFotoTomada(ruta)
        } else {
            ruta?.let { File(it).delete() } // Borra el archivo temporal si el usuario canceló
        }
        rutaFotoPendiente = null
    }

    fun tomarFoto() {
        try {
            val archivo = CameraHelper.crearArchivoFoto(context)
            rutaFotoPendiente = archivo.absolutePath
            camaraLauncher.launch(CameraHelper.uriDeArchivo(context, archivo))
        } catch (e: ActivityNotFoundException) {
            rutaFotoPendiente = null
            Toast.makeText(context, "Este dispositivo no tiene app de cámara", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Interfaz Material 3 ----------
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Default.AccessTime, contentDescription = "Hora") },
        title = {
            Text(
                text = "Registrar ${draft.tipo.lowercase()}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reloj digital
                Text(
                    text = hora,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Estado de Ubicación GPS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    when {
                        draft.cargandoUbicacion -> {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Obteniendo ubicación…", fontSize = 13.sp)
                        }
                        draft.latitud != null && draft.longitud != null -> {
                            Text(
                                text = "Ubicación lista",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        else -> {
                            Text(
                                text = draft.ubicacionError ?: "Sin ubicación",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }

                if (!draft.cargandoUbicacion && draft.latitud == null) {
                    TextButton(onClick = {
                        onReintentarUbicacion()
                        pedirUbicacion()
                    }) {
                        Text("Reintentar ubicación")
                    }
                }

                // Fotografía (Opcional)
                AnimatedVisibility(visible = draft.fotoPath != null) {
                    draft.fotoPath?.let { ruta ->
                        FotoMiniatura(
                            ruta = ruta,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            anchoMax = 600
                        )
                    }
                }

                TextButton(onClick = { tomarFoto() }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (draft.fotoPath == null) "Agregar foto (opcional)" else "Cambiar foto")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Feedback háptico
                    onConfirmar()
                },
                enabled = !draft.cargandoUbicacion
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

// Función privada para el formato de hora HH:mm:ss
private fun horaActual(): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
