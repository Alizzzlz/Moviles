package com.example.moviles.ui.view.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.R

val PrimaryDarkBlue = Color(0xFF493261)

private const val DURACION_SPLASH_MS = 3000

@Composable
fun SplashView(
    onFinished: () -> Unit = {}
) {
    val rotation = remember { Animatable(-180f) }
    val progreso = remember { Animatable(0f) }

    // Siempre llama a la versión más reciente del callback aunque la pantalla se recomponga
    val onFinishedActual by rememberUpdatedState(onFinished)

    // Animación del logo (gira de -180° a 0°)
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.9f, stiffness = 300f)
        )
    }

    // Barra de carga: se llena en 3 segundos y al terminar navega
    LaunchedEffect(Unit) {
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = DURACION_SPLASH_MS, easing = LinearEasing)
        )
        onFinishedActual()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDarkBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gato_bigote),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer { rotationZ = rotation.value }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Obra Check",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Control de Asistencia",
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Diseño de "cargando"
            LinearProgressIndicator(
                progress = { progreso.value },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cargando...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashViewPreview() {
    SplashView()
}

