package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1500,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")
    var targetValue by remember { mutableStateOf(500f) }
    val shimmerWidth = 400f
    val translateAnimation by transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = targetValue + shimmerWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                delayMillis = 0,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    val shimmerColor = ExtendedColorTheme.colorScheme.shimmerColor
    return drawBehind {
        if (this.size.width != targetValue)
            targetValue = this.size.width
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    shimmerColor.copy(alpha = 0.1f),
                    shimmerColor.copy(alpha = 0.1f),
                    shimmerColor.copy(alpha = 0.2f),
                    shimmerColor.copy(alpha = 0.35f),
                    shimmerColor.copy(alpha = 0.5f),
                    shimmerColor.copy(alpha = 0.6f),
                    shimmerColor.copy(alpha = 0.5f),
                    shimmerColor.copy(alpha = 0.35f),
                    shimmerColor.copy(alpha = 0.2f),
                    shimmerColor.copy(alpha = 0.1f),
                    shimmerColor.copy(alpha = 0.1f),
                ),
                start = Offset(x = translateAnimation, y = 0f),
                end = Offset(x = translateAnimation + shimmerWidth, y = 0f),
                TileMode.Clamp
            )
        )
    }
}

@Composable
fun BackHandler(backHandler: BackHandler, isEnabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val callback =
        remember {
            BackCallback(isEnabled = isEnabled) {
                currentOnBack()
            }
        }
    SideEffect { callback.isEnabled = isEnabled }
    DisposableEffect(backHandler) {
        backHandler.register(callback)
        onDispose { backHandler.unregister(callback) }
    }
}

@Composable
inline fun LimitSize(
    percentage: Float,
    crossinline content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints {
        Box(
            Modifier
                .sizeIn(
                    maxHeight = maxHeight * percentage,
                    maxWidth = maxWidth * percentage
                )
        ) {
            content()
        }
    }
}

/**
 * a simple infinite loading animation
 */
@Composable
fun LoadingCircle(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis, easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    CircularProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .aspectRatio(1f),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        trackColor = MaterialTheme.colorScheme.secondaryContainer,
        gapSize = 0.dp
    )
}

@Composable
fun <T> StateFlow<T>.collectValue(
    context: CoroutineContext = EmptyCoroutineContext
): T = collectAsState(
    context = context
).value
