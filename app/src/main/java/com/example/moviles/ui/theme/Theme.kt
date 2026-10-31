package com.example.moviles.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaColorOscuro = darkColorScheme(
    primary = BotonOscuro,
    onPrimary = TextoBotonOscuro,
    background = FondoOscuro,
    surface = TarjetaOscura,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    outline = BordeTarjetaOscuro,
    tertiary = BotonTextoOscuro,
    secondary = BotonTextoSecundarioOscuro,
    error = ColorError
)

private val EsquemaColorClaro = lightColorScheme(
    primary = BotonClaro,
    onPrimary = Color.White,
    background = FondoClaro,
    surface = CasillaClara,
    onBackground = TextoClaro,
    onSurface = TextoClaro,
    outline = BordeTarjetaClaro,
    tertiary = BotonTextoClaro,
    secondary = BotonTextoSecundarioClaro,
    error = ColorError
)

@Composable
fun MarkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colores = if (darkTheme) EsquemaColorOscuro else EsquemaColorClaro

    MaterialTheme(
        colorScheme = colores,
        typography = Typography,
        content = content
    )
}