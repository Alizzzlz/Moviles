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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.R
import com.example.moviles.ui.theme.MarkTheme

private const val DURACION_SPLASH_MS = 3000

@Composable
fun SplashView(
    onFinished: () -> Unit = {}
) {
    // Inicia en -360f para dar una vuelta limpia de 360° y quedar derecho (0f)
    val rotation = remember { Animatable(-360f) }
    val progreso = remember { Animatable(0f) }

    val onFinishedActual by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 1f, stiffness = 50f)
        )
    }

    LaunchedEffect(Unit) {
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = DURACION_SPLASH_MS, easing = LinearEasing)
        )
        onFinishedActual()
    }

    val fondoRadial = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.90f) // Subimos un poco el alpha a 0.90f para mayor intensidad
        ),
        radius = 3200f
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = fondoRadial)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app2),
                contentDescription = "Logo MARK",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { rotationZ = rotation.value }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Control de Asistencia",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            LinearProgressIndicator(
                progress = { progreso.value },
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Iniciando...",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "MARK v1.0 • Gestión de Obra",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Modo Claro")
@Composable
private fun SplashViewLightPreview() {
    MarkTheme(darkTheme = false) {
        SplashView()
    }
}

@Preview(showBackground = true, name = "Modo Oscuro")
@Composable
private fun SplashViewDarkPreview() {
    MarkTheme(darkTheme = true) {
        SplashView()
    }
}