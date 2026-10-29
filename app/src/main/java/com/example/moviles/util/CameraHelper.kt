package com.example.moviles.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Utilidades para tomar fotos con la cámara y mostrarlas. */
object CameraHelper {

    /** Crea un archivo vacío en files/marcajes/ donde la cámara guardará la foto. */
    fun crearArchivoFoto(context: Context): File {
        val carpeta = File(context.filesDir, "marcajes").apply { mkdirs() }
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(carpeta, "marcaje_$marca.jpg")
    }

    /** La cámara no puede leer nuestra carpeta privada; el FileProvider le entrega un permiso temporal. */
    fun uriDeArchivo(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Carga la foto reducida (para no gastar memoria) y corrige su rotación. */
    fun cargarBitmap(ruta: String, anchoMax: Int = 400): Bitmap? {
        val archivo = File(ruta)
        if (!archivo.exists()) return null

        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, limites)

        var muestreo = 1
        while (limites.outWidth / muestreo > anchoMax * 2) muestreo *= 2

        val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
        val bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null

        val grados = try {
            when (ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            0f
        }
        if (grados == 0f) return bitmap

        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }
}
