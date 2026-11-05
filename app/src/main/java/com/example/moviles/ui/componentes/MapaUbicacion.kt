package com.example.moviles.ui.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.util.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Mapa REAL de Google Maps con un marcador en las coordenadas indicadas.
 * Si no hay internet, Google Maps muestra el mapa en blanco/gris: por eso se avisa con texto.
 */
@Composable
fun MapaUbicacion(
    latitud: Double,
    longitud: Double,
    titulo: String,
    modifier: Modifier = Modifier,
    zoom: Float = 17f,
    mostrarPuntoAzul: Boolean = true
) {
    val context = LocalContext.current
    val posicion = LatLng(latitud, longitud)
    val estadoRed by rememberEstadoRed()

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicion, zoom)
    }
    val marcador = remember(latitud, longitud) { MarkerState(position = posicion) }

    // Si el GPS se afina y cambian las coordenadas, la cámara del mapa se mueve sola
    LaunchedEffect(latitud, longitud) {
        camara.animate(CameraUpdateFactory.newLatLngZoom(posicion, zoom))
    }

    Box(modifier = modifier.clip(RoundedCornerShape(12.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camara,
            properties = MapProperties(
                // isMyLocationEnabled lanza SecurityException si no hay permiso de ubicación
                isMyLocationEnabled = mostrarPuntoAzul && LocationHelper.tienePermiso(context)
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            Marker(state = marcador, title = titulo)
        }

        if (!estadoRed.conectado) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Sin internet: el mapa no puede cargar",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
