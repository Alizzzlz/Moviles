package com.example.moviles.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object LocationHelper {

    fun tienePermiso(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /** true si el GPS / servicio de ubicación del teléfono está encendido. */
    fun gpsActivo(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    /**
     * Pide una ubicación NUEVA (getCurrentLocation). Antes se usaba lastLocation, que
     * devuelve null si el teléfono no tiene una ubicación reciente en caché.
     * Si falla, intenta con la última conocida.
     */
    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacion(context: Context): Pair<Double, Double>? {
        if (!tienePermiso(context)) return null

        val client = LocationServices.getFusedLocationProviderClient(context)
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val prioridad = if (fine) Priority.PRIORITY_HIGH_ACCURACY
        else Priority.PRIORITY_BALANCED_POWER_ACCURACY

        val actual: Pair<Double, Double>? = suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }
            try {
                client.getCurrentLocation(prioridad, cts.token)
                    .addOnSuccessListener { loc ->
                        if (cont.isActive) cont.resume(loc?.let { it.latitude to it.longitude })
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
            } catch (e: SecurityException) {
                if (cont.isActive) cont.resume(null)
            }
        }
        if (actual != null) return actual

        // Plan B: última ubicación conocida
        return suspendCancellableCoroutine { cont ->
            try {
                client.lastLocation
                    .addOnSuccessListener { loc ->
                        if (cont.isActive) cont.resume(loc?.let { it.latitude to it.longitude })
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
            } catch (e: SecurityException) {
                if (cont.isActive) cont.resume(null)
            }
        }
    }
}













