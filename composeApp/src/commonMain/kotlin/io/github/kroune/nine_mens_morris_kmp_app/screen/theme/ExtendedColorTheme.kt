package io.github.kroune.nine_mens_morris_kmp_app.screen.theme

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun lightExtendedColors(): ExtendedColorScheme {
    return ExtendedColorScheme(
        linkColors = Color(85, 104, 218),
        shimmerColor = Color.DarkGray,
        animationScreenBackground = Color.White,
        animationScreenForeground = Color.DarkGray,
        colorPiece1 = Color.Black,
        colorPiece2 = Color.White,
    )
}

fun darkExtendedColors(): ExtendedColorScheme {
    return ExtendedColorScheme(
        linkColors = Color(79, 120, 255),
        shimmerColor = Color.LightGray,
        animationScreenBackground = Color.Gray,
        animationScreenForeground = Color.Black,
        colorPiece1 = Color.Black,
        colorPiece2 = Color.White,
    )
}

@Immutable
data class ExtendedColorScheme(
    val linkColors: Color,
    val shimmerColor: Color,
    val animationScreenBackground: Color,
    val animationScreenForeground: Color,
    val colorPiece1: Color,
    val colorPiece2: Color,
)

internal val LocalColorScheme = staticCompositionLocalOf { lightExtendedColors() }

@Composable
fun ExtendedColorTheme(
    colorScheme: ExtendedColorScheme = lightExtendedColors(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
    ) {
        content()
    }
}

object ExtendedColorTheme {
    val colorScheme: ExtendedColorScheme
        @Composable @ReadOnlyComposable get() = LocalColorScheme.current
}