package com.example.moviles.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Acceso al GPS del teléfono usando Google Play Services (FusedLocationProvider). */
object LocationHelper {

    fun tienePermiso(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Devuelve (latitud, longitud) o null si no se pudo obtener.
     * Es "suspend": espera la respuesta del GPS sin congelar la pantalla.
     */
    @SuppressLint("MissingPermission") // el permiso se comprueba antes con tienePermiso()
    suspend fun obtenerUbicacionActual(context: Context): Pair<Double, Double>? {
        if (!tienePermiso(context)) return null
        val cliente = LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { cont ->
            val cancelacion = CancellationTokenSource()
            cont.invokeOnCancellation { cancelacion.cancel() }

            cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelacion.token)
                .addOnSuccessListener { ubicacion ->
                    if (ubicacion != null) {
                        if (cont.isActive) cont.resume(Pair(ubicacion.latitude, ubicacion.longitude))
                    } else {
                        // Plan B: última ubicación conocida
                        cliente.lastLocation
                            .addOnSuccessListener { ultima ->
                                if (cont.isActive) cont.resume(ultima?.let { Pair(it.latitude, it.longitude) })
                            }
                            .addOnFailureListener { if (cont.isActive) cont.resume(null) }
                    }
                }
                .addOnFailureListener { if (cont.isActive) cont.resume(null) }
        }
    }
}
