package com.example.moviles.ui.view.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// --- Paleta de colores solicitada ---
val PrimaryBlue = Color(0xFF378DD2)
val PrimaryDarkBlue = Color(0xFF493261)
val SurfaceBlue = Color(0xFF2A4B7C)
val SuccessGreen = Color(0xFF2D7831)

// Modelo para estructurar las páginas del Onboarding
data class PageOnboarding(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

@Preview(showBackground = true)
@Composable
fun OnboardingView(
    onNavegarARegistro: () -> Unit = {}
) {
    // Lista de las pantallas de bienvenida
    val paginas = listOf(
        PageOnboarding(
            titulo = "Control de Marcaje",
            descripcion = "Esta aplicación fue creada para llevar un registro limpio y ordenado de marcaje de todos los trabajadores.",
            icono = Icons.Default.AssignmentTurnedIn
        ),
        PageOnboarding(
            titulo = "¡Comencemos!",
            descripcion = "Vamos a registrarte para que puedas acceder a tu perfil y comenzar a marcar tu asistencia.",
            icono = Icons.Default.HowToReg
        )
    )

    // Estado del pager y scope de corrutinas para controlar el botón "Siguiente"
    val pagerState = rememberPagerState(pageCount = { paginas.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDarkBlue)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Carrusel deslizable de páginas
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { position ->
            val item = paginas[position]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Contenedor visual del ícono
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(SurfaceBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = item.titulo,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.descripcion,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // 2. Indicadores de posición (Puntos/Dots)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(paginas.size) { index ->
                val esPaginaActual = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (esPaginaActual) 24.dp else 10.dp, height = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (esPaginaActual) PrimaryBlue else SurfaceBlue)
                )
            }
        }

        // 3. Botón de acción (Cambia de "Siguiente" a "Registrarme")
        val esUltimaPagina = pagerState.currentPage == paginas.size - 1

        Button(
            onClick = {
                if (esUltimaPagina) {
                    onNavegarARegistro()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (esUltimaPagina) SuccessGreen else PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (esUltimaPagina) "Registrarme" else "Siguiente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}