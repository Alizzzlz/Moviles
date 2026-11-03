package com.example.moviles.ui.view.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

/**
 * Diálogo "Confirmar entrada / salida / colación".
 * - Pide permiso de ubicación y obtiene el GPS (es opcional: se puede confirmar sin ubicación).
 * - Permite tomar una foto de respaldo (opcional) con la app de cámara del teléfono.
 *
 * El estado (MarcajeDraft) vive en AsistenciaViewModel; este composable solo lo dibuja.
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

    // Ruta de la foto en curso; sobrevive a rotación / cierre de la app mientras se usa la cámara
    var rutaFotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    // Cada vez que sube este contador se lanza una búsqueda de GPS
    var busquedas by remember { mutableIntStateOf(0) }
    var denegaciones by remember { mutableIntStateOf(0) }

    // ---------- GPS y permisos ----------
    val permisosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            busquedas++
        } else {
            denegaciones++
            onUbicacionError("Permiso de ubicación denegado. Puedes confirmar sin ubicación.")
        }
    }

    fun buscarUbicacion() {
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

    // Al abrir el diálogo, busca la ubicación automáticamente
    LaunchedEffect(Unit) {
        if (draft.cargandoUbicacion) buscarUbicacion()
    }

    LaunchedEffect(busquedas) {
        if (busquedas > 0) {
            if (!LocationHelper.gpsActivo(context)) {
                onUbicacionError("El GPS está apagado. Actívalo o confirma sin ubicación.")
            } else {
                val ubicacion = withTimeoutOrNull(15_000L) {
                    LocationHelper.obtenerUbicacion(context)
                }
                if (ubicacion != null) {
                    onUbicacionObtenida(ubicacion.first, ubicacion.second)
                } else {
                    onUbicacionError("No se pudo obtener la ubicación. Puedes confirmar sin ubicación.")
                }
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
            ruta?.let { File(it).delete() } // el usuario canceló: borra el archivo vacío
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
            Toast.makeText(context, "Este teléfono no tiene app de cámara", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            rutaFotoPendiente = null
            Toast.makeText(context, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    fun abrirAjustesApp() {
        try {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            )
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudieron abrir los ajustes", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Interfaz ----------
    AlertDialog(
        onDismissRequest = onCancelar,
        title = {
            Text(
                text = "Confirmar ${draft.tipo.lowercase()}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Ubicación ---
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ubicación", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        when {
                            draft.cargandoUbicacion -> Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Obteniendo ubicación…", fontSize = 13.sp)
                            }

                            draft.latitud != null && draft.longitud != null -> Text(
                                text = "Ubicación obtenida ✓",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            else -> Text(
                                text = draft.ubicacionError ?: "Sin ubicación",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    if (!draft.cargandoUbicacion && draft.latitud == null) {
                        TextButton(onClick = {
                            onReintentarUbicacion()
                            // Si ya la denegó 2 veces Android no vuelve a preguntar: manda a Ajustes
                            if (denegaciones >= 2 && !LocationHelper.tienePermiso(context)) {
                                abrirAjustesApp()
                                onUbicacionError("Activa el permiso de Ubicación en Ajustes y vuelve a intentar.")
                            } else {
                                buscarUbicacion()
                            }
                        }) {
                            Text(if (denegaciones >= 2) "Ajustes" else "Reintentar")
                        }
                    }
                }

                // --- Foto ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Foto de respaldo (opcional)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                if (draft.fotoPath != null) {
                    FotoMiniatura(
                        ruta = draft.fotoPath,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        anchoMax = 600
                    )
                }

                OutlinedButton(
                    onClick = { tomarFoto() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (draft.fotoPath == null) "Tomar foto" else "Cambiar foto")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onConfirmar()
                },
                enabled = !draft.cargandoUbicacion
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}











