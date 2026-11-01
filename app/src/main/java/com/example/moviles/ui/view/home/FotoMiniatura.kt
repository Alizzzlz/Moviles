package com.example.moviles.ui.view.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Muestra una foto guardada en el teléfono decodificándola en segundo plano. */
@Composable
fun FotoMiniatura(
    ruta: String,
    modifier: Modifier = Modifier,
    anchoMax: Int = 300
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = ruta) {
        value = withContext(Dispatchers.IO) {
            try {
                val archivo = File(ruta)
                if (!archivo.exists()) return@withContext null

                // Decodificar dimensiones primero para escalar inteligentemente
                val opciones = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(ruta, opciones)

                // Calcular el factor de escala en base al ancho deseado
                var scale = 1
                while (opciones.outWidth / scale / 2 >= anchoMax) {
                    scale *= 2
                }

                // Decodificar el bitmap optimizado
                val opcionesFinales = BitmapFactory.Options().apply {
                    inSampleSize = scale
                }
                BitmapFactory.decodeFile(ruta, opcionesFinales)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
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
