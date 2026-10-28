package com.example.moviles.ui.view.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.ui.theme.ColorExito
import com.example.moviles.ui.theme.ObraCheckTheme

enum class TipoNotificacion {
    EXITO,
    ERROR
}

data class NotificacionItem(
    val id: Int,
    val titulo: String,
    val mensaje: String,
    val hora: String,
    val tipo: TipoNotificacion
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListView(
    onVolverAHome: () -> Unit = {}
) {
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

    val colorEstado = if (esError) MaterialTheme.colorScheme.error else ColorExito
    val iconoEstado = if (esError) Icons.Default.Error else Icons.Default.CheckCircle

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colorEstado.copy(alpha = 0.15f)),
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
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = notificacion.hora,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notificacion.mensaje,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Modo Claro")
@Composable
private fun NotificationListViewLightPreview() {
    ObraCheckTheme(darkTheme = false) {
        NotificationListView()
    }
}

@Preview(showBackground = true, name = "Modo Oscuro")
@Composable
private fun NotificationListViewDarkPreview() {
    ObraCheckTheme(darkTheme = true) {
        NotificationListView()
    }
}