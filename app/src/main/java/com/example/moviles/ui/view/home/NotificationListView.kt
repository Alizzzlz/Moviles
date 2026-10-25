package com.example.moviles.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



// Alertas, estados y bordes
val ErrorRed = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF2D7831)
val CardBorderGray = Color(0xFFE0E0E0)

// Tipos de notificación para controlar estilos e íconos
enum class TipoNotificacion {
    EXITO,
    ERROR
}

// Modelo de datos para las notificaciones
data class NotificacionItem(
    val id: Int,
    val titulo: String,
    val mensaje: String,
    val hora: String,
    val tipo: TipoNotificacion
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NotificationListView(
    onVolverAHome: () -> Unit = {}
) {
    // Lista con las notificaciones requeridas
    val listaNotificaciones = listOf(
        NotificacionItem(
            id = 1,
            titulo = "Error de Marcaje",
            mensaje = "Hubo un problema con el marcaje de hora de salida, favor contacta a su supervisor.",
            hora = "Hace 10 min",
            tipo = TipoNotificacion.ERROR
        ),
        NotificacionItem(
            id = 2,
            titulo = "Hora Extra Aprobada",
            mensaje = "Hora extra registrada con éxito.",
            hora = "Hace 2 horas",
            tipo = TipoNotificacion.EXITO
        ),
        NotificacionItem(
            id = 3,
            titulo = "Jornada Finalizada",
            mensaje = "Salida marcada con éxito.",
            hora = "Ayer",
            tipo = TipoNotificacion.EXITO
        ),
        NotificacionItem(
            id = 4,
            titulo = "Inicio de Jornada",
            mensaje = "Entrada registrada con éxito.",
            hora = "Ayer",
            tipo = TipoNotificacion.EXITO
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = SurfaceBlue
                )
            )
        },
        containerColor = PrimaryDarkBlue
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(listaNotificaciones) { notificacion ->
                ItemNotificacionCard(notificacion = notificacion)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ItemNotificacionCard(notificacion: NotificacionItem) {
    val esError = notificacion.tipo == TipoNotificacion.ERROR

    // Color del ícono y acentos según el tipo de notificación
    val colorEstado = if (esError) ErrorRed else SuccessGreen
    val iconoEstado = if (esError) Icons.Default.Error else Icons.Default.CheckCircle

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardBorderGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            ),
        colors = cardColors(containerColor = SurfaceBlue),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Contenedor del ícono circular con el color correspondiente (Verde o Rojo)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colorEstado.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoEstado,
                    contentDescription = null,
                    tint = colorEstado,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Detalle de la notificación
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacion.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = notificacion.hora,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notificacion.mensaje,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}