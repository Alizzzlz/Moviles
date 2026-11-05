package com.example.moviles.ui.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.util.EstadoRed
import com.example.moviles.util.NetworkHelper

/** Estado de red que se actualiza solo cuando cambia el Wi-Fi / los datos móviles. */
@Composable
fun rememberEstadoRed(): State<EstadoRed> {
    val context = LocalContext.current
    val flujo = remember(context) { NetworkHelper.observarRed(context) }
    return flujo.collectAsState(initial = NetworkHelper.estadoActual(context))
}

/** Franja roja que aparece solo cuando NO hay internet. Al tocarla abre los ajustes de Wi-Fi. */
@Composable
fun EstadoRedBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val estado by rememberEstadoRed()

    if (!estado.conectado) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = modifier
                .fillMaxWidth()
                .clickable { NetworkHelper.abrirAjustesWifi(context) }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Sin internet. Los marcajes se guardan en el teléfono. Toca para abrir Wi-Fi.",
                    fontSize = 12.sp
                )
            }
        }
    }
}
