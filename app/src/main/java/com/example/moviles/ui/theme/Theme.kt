package com.example.moviles.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//modo claro
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = CardSoftPurpleBlue,    // Para tarjetas de resumen e historial
    onPrimaryContainer = OnCardSoftText,       // Texto e íconos dentro de las tarjetas de resumen
    secondary = SecondaryAmber,
    onSecondary = Color.White,
    background = PrimaryDarkBlue,             // Fondo oscuro de la vista completa
    onBackground = Color.White,               // Texto blanco para "Bienvenido, Juan Pérez"
    surface = SurfaceLight,                   // Tarjeta blanca principal y barras
    onSurface = OnBackgroundDark,             // Texto oscuro en tarjetas blancas
    onSurfaceVariant = OnSurfaceVariant
)

//modo oscuro
private val DarkColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = CardSoftPurpleBlue,
    onPrimaryContainer = OnCardSoftText,
    secondary = SecondaryAmber,
    background = PrimaryDarkBlue,
    onBackground = Color.White,
    surface = SurfaceLight,
    onSurface = OnBackgroundDark,
    onSurfaceVariant = OnSurfaceVariant
)

@Composable
fun MovilesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}