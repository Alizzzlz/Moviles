package com.example.moviles.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraHelper {

    /** Crea un archivo vacío en filesDir/fotos/ (coincide con res/xml/file_paths.xml). */
    fun crearArchivoFoto(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val carpeta = File(context.filesDir, "fotos")
        if (!carpeta.exists()) carpeta.mkdirs()
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", carpeta)
    }

    fun uriDeArchivo(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}


