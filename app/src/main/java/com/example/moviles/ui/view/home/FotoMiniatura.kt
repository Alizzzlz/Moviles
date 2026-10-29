package com.example.moviles.ui.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.example.moviles.util.CameraHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Muestra una foto guardada en el teléfono. La decodifica en segundo plano para no trabar la pantalla. */
@Composable
fun FotoMiniatura(
    ruta: String,
    modifier: Modifier = Modifier,
    anchoMax: Int = 300
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, ruta) {
        value = withContext(Dispatchers.IO) { CameraHelper.cargarBitmap(ruta, anchoMax)?.asImageBitmap() }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Foto del marcaje",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
