package com.example.moviles.ui.view.splash

import android.window.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(){
    LaunchedEffect(key1 = true) {
        delay(3000)
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    )
}

