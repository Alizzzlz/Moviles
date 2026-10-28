package com.example.moviles.ui.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordEmailView(
    onNavigateToPhone: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onSendCode: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Aplica FondoClaro / FondoOscuro
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground // Aplica TextoClaro / TextoOscuro
            )

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Ingresa tu correo corporativo y te enviaremos un enlace para restablecer tu cuenta.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary, // Aplica TextoSecundarioClaro / TextoSecundarioOscuro
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@constructora.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,      // Bordes activos
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,    // BordeTarjetaClaro / BordeTarjetaOscuro
                    focusedLabelColor = MaterialTheme.colorScheme.primary,       // Label enfocado
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,   // Label en reposo
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,   // Texto escrito
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,   // CasillaClara / TarjetaOscura
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onSendCode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,   // BotonClaro / BotonOscuro
                    contentColor = MaterialTheme.colorScheme.onPrimary     // TextoBotonOscuro / White
                )
            ) {
                Text(
                    text = "Enviar solicitud",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onNavigateToPhone() }
            ) {
                Text(
                    text = "Probar con número de teléfono",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary // Usa BotonTextoSecundario
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = { onNavigateToLogin() }
            ) {
                Text(
                    text = "Volver al inicio de sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary // Usa BotonTextoClaro / BotonTextoOscuro
                )
            }
        }
    }
}