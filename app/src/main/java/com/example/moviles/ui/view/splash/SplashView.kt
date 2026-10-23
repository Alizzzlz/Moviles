package com.example.moviles.ui.view.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviles.R
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer


val Purple40 = Color(0xFF6650a4)


@Preview(showBackground = true)
@Composable
fun SplashView(){
    val rotation = remember { Animatable(-180f) }
    LaunchedEffect(key1 = true) {
        // Anima de -180° a 0° con un resorte espacial
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = 0.9f,  // <1 = rebota un poco antes de asentarse
                stiffness = 300f      // Slow spatial: movimiento más pausado
            )
        )
        delay(3000)
        // Aquí iría la navegación a la siguiente pantalla
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Purple40),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gato_bigote),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp).graphicsLayer{ rotationZ = rotation.value }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Obra Check",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color=Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Control de Asistencia",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

