package com.example.moviles.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class TipoRed(val etiqueta: String) {
    WIFI("Wi-Fi"),
    DATOS_MOVILES("Datos móviles"),
    ETHERNET("Ethernet"),
    OTRA("Conectado"),
    SIN_CONEXION("Sin conexión")
}

data class EstadoRed(
    val conectado: Boolean,
    val tipo: TipoRed
) {
    companion object {
        val SIN_CONEXION = EstadoRed(false, TipoRed.SIN_CONEXION)
    }
}

/**
 * Detecta si el teléfono tiene internet y por qué medio (Wi-Fi o datos móviles).
 * Requiere los permisos ACCESS_NETWORK_STATE (y ACCESS_WIFI_STATE) del Manifest.
 */
object NetworkHelper {

    private fun convertir(caps: NetworkCapabilities?): EstadoRed {
        if (caps == null || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            return EstadoRed.SIN_CONEXION
        }
        // VALIDATED = Android comprobó que realmente hay salida a internet
        // (un Wi-Fi sin internet aparece como conectado pero NO validado)
        val validado = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val tipo = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> TipoRed.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> TipoRed.DATOS_MOVILES
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> TipoRed.ETHERNET
            else -> TipoRed.OTRA
        }
        return if (validado) EstadoRed(true, tipo) else EstadoRed(false, tipo)
    }

    /** Estado de la red en este instante. */
    fun estadoActual(context: Context): EstadoRed {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return EstadoRed.SIN_CONEXION
        val red = cm.activeNetwork ?: return EstadoRed.SIN_CONEXION
        return convertir(cm.getNetworkCapabilities(red))
    }

    fun hayInternet(context: Context): Boolean = estadoActual(context).conectado

    /** Emite un nuevo estado cada vez que el teléfono gana, pierde o cambia de red. */
    fun observarRed(context: Context): Flow<EstadoRed> = callbackFlow {
        val cm = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm == null) {
            trySend(EstadoRed.SIN_CONEXION)
            close()
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(convertir(caps))
            }

            override fun onLost(network: Network) {
                trySend(EstadoRed.SIN_CONEXION)
            }
        }

        trySend(estadoActual(context)) // valor inicial
        try {
            cm.registerDefaultNetworkCallback(callback)
        } catch (e: SecurityException) {
            trySend(EstadoRed.SIN_CONEXION)
        }
        awaitClose {
            try {
                cm.unregisterNetworkCallback(callback)
            } catch (_: Exception) {
            }
        }
    }.distinctUntilChanged()

    /** Abre los ajustes de Wi-Fi del teléfono. */
    fun abrirAjustesWifi(context: Context) {
        try {
            context.startActivity(
                Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
