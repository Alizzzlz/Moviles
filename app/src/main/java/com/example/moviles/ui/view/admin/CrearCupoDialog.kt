package com.example.moviles.ui.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CrearCupoDialog(
    onDismiss: () -> Unit,
    onPublicar: (tarea: String, horas: String, cupos: String, fecha: String) -> Unit
) {
    var tarea by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var cupos by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Cupo Hora Extra") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tarea,
                    onValueChange = { tarea = it },
                    label = { Text("Tarea / Motivo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (Ej: Sábado 15 de Nov)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = horas,
                    onValueChange = { horas = it },
                    label = { Text("Cantidad de Horas") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cupos,
                    onValueChange = { cupos = it },
                    label = { Text("Cupos Totales") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tarea.isNotBlank() && horas.isNotBlank() && cupos.isNotBlank() && fecha.isNotBlank()) {
                        onPublicar(tarea, horas, cupos, fecha)
                    }
                }
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}