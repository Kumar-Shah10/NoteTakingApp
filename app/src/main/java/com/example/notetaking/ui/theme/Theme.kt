package com.example.notetaking.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoteTakingLightColors = lightColorScheme(
    primary = Gold,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = SurfaceField,
    onSurface = TextPrimary,
    error = ErrorText,
)

@Composable
fun NoteTakingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NoteTakingLightColors,
        content = content,
    )
}