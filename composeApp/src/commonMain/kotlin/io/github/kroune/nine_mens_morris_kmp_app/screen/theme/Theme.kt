package io.github.kroune.nine_mens_morris_kmp_app.screen.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
inline fun AppTheme(crossinline content: @Composable () -> Unit) {
    val colorScheme = when (isSystemInDarkTheme()) {
        false -> lightColorScheme(
            primary = Color.Black,
            background = Color(255, 255, 255),
            secondaryContainer = Color(0, 0, 0, 80),
            surface = Color.Black,
            inversePrimary = Color.White,
        )

        true -> darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color(red = 58, green = 58, blue = 58),
            surface = Color.White,
            inversePrimary = Color.Black,
        )
    }
    val extendedColorScheme = when (isSystemInDarkTheme()) {
        false -> lightExtendedColors()
        true -> darkExtendedColors()
    }
    MaterialTheme(colorScheme) {
        ExtendedColorTheme(extendedColorScheme) {
            content()
        }
    }
}