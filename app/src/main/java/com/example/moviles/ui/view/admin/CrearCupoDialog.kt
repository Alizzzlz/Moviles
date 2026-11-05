package com.example.moviles.ui.view.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCupoDialog(
    onDismiss: () -> Unit,
    onPublicar: (tarea: String, horas: String, cupos: String, fecha: String) -> Unit
) {
    var tarea by remember { mutableStateOf("") }
    var horas by remember { mutableStateOf("") }
    var cupos by remember { mutableStateOf("") }
    var fechaTexto by remember { mutableStateOf("") }

    var mostrarDatePicker by remember { mutableStateOf(false) }

    // Estado para el selector de fecha de Material 3
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Cupo Hora Extra") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tarea,
                    onValueChange = { tarea = it },
                    label = { Text("Tarea / Motivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de texto de Fecha que abre el Calendario al hacer clic
                Box {
                    OutlinedTextField(
                        value = fechaTexto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha del Cupo") },
                        placeholder = { Text("Seleccionar fecha...") },
                        trailingIcon = {
                            IconButton(onClick = { mostrarDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Transparente por encima para capturar el clic en todo el campo
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { mostrarDatePicker = true }
                    )
                }

                OutlinedTextField(
                    value = horas,
                    onValueChange = { horas = it },
                    label = { Text("Cantidad de Horas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cupos,
                    onValueChange = { cupos = it },
                    label = { Text("Cupos Totales") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tarea.isNotBlank() && horas.isNotBlank() && cupos.isNotBlank() && fechaTexto.isNotBlank()) {
                        onPublicar(tarea, horas, cupos, fechaTexto)
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

    // Modal con el Calendario de Material 3
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Ajustamos zona horaria UTC para evitar desfases de día al formatear
                            val sdf = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "CL"))
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            fechaTexto = sdf.format(Date(millis)).replaceFirstChar { it.uppercase() }
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}