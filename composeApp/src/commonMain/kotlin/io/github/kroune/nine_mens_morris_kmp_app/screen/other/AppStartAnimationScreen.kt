package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponentI
import io.github.kroune.nine_mens_morris_kmp_app.event.other.AppStartAnimationScreenEvent
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.press_to_start
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AppStartAnimationScreen(
    component: AppStartAnimationComponentI
) {
    DrawAnimation()
    StartButton(component)
}


/**
 * draw good looking animation
 * can be used when loading data
 */
@Composable
private fun DrawAnimation() {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 1f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7500, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundAnimation"
    )
    val colorScheme = MaterialTheme.colorScheme
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val lightPath = Path()
        for (x in 0 until size.width.toInt() step 10) {
            val y = f(x, animatedProgress).coerceIn(0f..size.height)
            lightPath.lineTo(x.toFloat(), size.height - y)
        }
        val y = f(size.width.toInt(), animatedProgress).coerceIn(0f..size.height)
        lightPath.lineTo(size.width, size.height - y)
        lightPath.lineTo(size.width, size.height)
        lightPath.lineTo(0f, size.height)
        drawPath(
            path = lightPath,
            color = colorScheme.onBackground
        )
    }
}

/**
 * draws animation for the start button
 * it should be transparent if some important operation is in progress
 */
@Composable
private fun StartButton(
    component: AppStartAnimationComponentI
) {
    val infiniteScale = rememberInfiniteTransition(label = "buttonAnimation")
    val animatedProgress by infiniteScale.animateFloat(
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = CubicBezierEasing(0.2f, 0.0f, 0.4f, 1.0f)
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonAnimation"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.press_to_start),
            modifier = Modifier
                .alpha(animatedProgress)
                .clickable {
                    component.onEvent(AppStartAnimationScreenEvent.ClickButton)
                },
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 22.sp
        )
    }
}

/**
 * this is basically 1.55*x*a+300*sin(0.005*x)/|a+1|
 *
 * @param x - x coordinate
 * @param animationProgress - progress of the animation
 */
private fun f(x: Int, animationProgress: Float = 1f): Float {
    return (1.55f * x * animationProgress + 300 * sin(0.005f * x) / abs(animationProgress + 1))
}