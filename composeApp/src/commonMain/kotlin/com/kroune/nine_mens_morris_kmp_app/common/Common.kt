package com.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.ktor.client.HttpClient

class BlackGrayColors : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Black)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Gray)
    }
}

class TransparentColors : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }
}

val network = HttpClient()

/**
 * The server's address.
 * put your network ip here
 */
const val SERVER_ADDRESS = "://nine-men-s-morris.me"

/**
 * The API endpoint for user-related operations.
 */
const val USER_API = "/api/v1/user"

/**
 * a simple infinite loading animation
 */
@Composable
fun LoadingCircle() {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    CircularProgressIndicator(
        progress = animatedProgress
    )
}

val triangleShape: TriangleShape = TriangleShape()

/**
 * triangle shape
 * looks like this
 * -------
 *   -----
 *     ---
 *       -
 */
class TriangleShape : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val x = size.width
                val y = size.height

                moveTo(0f, 0f)
                lineTo(x, 0f)
                lineTo(x, y)
            }
        )
    }
}

/**
 * parallelogram shape
 */
class ParallelogramShape(
    private val bottomLineLeftOffset: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val x = size.width
                val y = size.height

                // top line
                moveTo(0f, 0f)
                lineTo(x, 0f)
                // bottom line
                lineTo(x, y)
                lineTo(x - bottomLineLeftOffset, y)
            }
        )
    }
}

@Composable
inline fun AppTheme(function: BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7E7E7E))
    ) {
        function()
    }
}
