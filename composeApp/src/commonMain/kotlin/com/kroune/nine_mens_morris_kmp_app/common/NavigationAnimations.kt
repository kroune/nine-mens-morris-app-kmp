package com.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.isFinished
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.router.stack.StackNavigator
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent

class CustomStackAnimator(
    private val animationSpec: FiniteAnimationSpec<Float> = tween(),
    private val frame: @Composable (factor: Float, direction: Direction, content: @Composable (Modifier) -> Unit) -> Unit,
    private val invertDirection: Boolean = false
) : StackAnimator {

    @Composable
    override operator fun invoke(
        direction: Direction,
        isInitial: Boolean,
        onFinished: () -> Unit,
        content: @Composable (Modifier) -> Unit,
    ) {
        val animationState = remember(
            direction,
            isInitial
        ) { AnimationState(initialValue = if (isInitial) 0F else 1F) }

        LaunchedEffect(animationState) {
            animationState.animateTo(
                targetValue = 0F,
                animationSpec = animationSpec,
                sequentialAnimation = !animationState.isFinished,
            )

            onFinished()
        }

        val factor =
            when (direction) {
                Direction.ENTER_FRONT -> animationState.value
                Direction.EXIT_FRONT -> 1F - animationState.value
                Direction.ENTER_BACK -> -animationState.value
                Direction.EXIT_BACK -> animationState.value - 1F
            } * if (invertDirection) -1 else 1

        frame(factor, direction, content)
    }
}

/**
 * A simple sliding animation. Children enter from one side and exit to another side.
 */
fun customSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    orientation: Orientation = Orientation.Horizontal,
    invertDirection: Boolean = false
): StackAnimator =
    CustomStackAnimator(
        animationSpec = animationSpec, { factor, _, content ->
            content(
                when (orientation) {
                    Orientation.Horizontal -> Modifier.offsetXFactor(factor)
                    Orientation.Vertical -> Modifier.offsetYFactor(factor)
                }
            )
        },
        invertDirection = invertDirection
    )


private fun Modifier.offsetXFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
        }
    }

private fun Modifier.offsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = (placeable.height.toFloat() * factor).toInt())
        }
    }

inline fun StackNavigator<RootComponent.Configuration>.pop(
    animation: StackAnimator,
    crossinline onComplete: (isSuccess: Boolean) -> Unit = {}
) {
    navigate(
        transformer = { stack ->
            stack.takeIf { it.size >= 2 }?.dropLast(1)?.let {
                val last = it.last()
                it - last + last.apply { this.animation = animation }
            } ?: stack
        },
        onComplete = { newStack, oldStack -> onComplete(newStack.size < oldStack.size) },
    )
}
