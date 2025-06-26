package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimator
import com.arkivanov.decompose.router.stack.StackNavigator
import io.github.kroune.nine_mens_morris_kmp_app.navigation.AnimateAbleConfiguration

/**
 * A simple sliding animation. Children enter from one side and exit to another side.
 */
@OptIn(ExperimentalDecomposeApi::class)
fun invertedSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    orientation: Orientation = Orientation.Horizontal
): StackAnimator = stackAnimator(animationSpec) { factor, _ ->
    when (orientation) {
        Orientation.Horizontal -> Modifier.invertedOffsetXFactor(factor)
        Orientation.Vertical -> Modifier.invertedOffsetYFactor(factor)
    }
}


private fun Modifier.invertedOffsetXFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = (-placeable.width.toFloat() * factor).toInt(), y = 0)
        }
    }

private fun Modifier.invertedOffsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = (-placeable.height.toFloat() * factor).toInt())
        }
    }

@OptIn(ExperimentalDecomposeApi::class)
inline fun <T : AnimateAbleConfiguration> StackNavigator<T>.pop(
    animation: StackAnimator,
    crossinline onComplete: (isSuccess: Boolean) -> Unit = {}
) {
    navigate(
        transformer = { stack ->
            stack.takeIf { it.size >= 2 }?.dropLast(1)?.let {
                val last = it.last()
                it - last + last.apply { this.customAnimation = animation }
            } ?: stack
        },
        onComplete = { newStack, oldStack -> onComplete(newStack.size < oldStack.size) },
    )
}
