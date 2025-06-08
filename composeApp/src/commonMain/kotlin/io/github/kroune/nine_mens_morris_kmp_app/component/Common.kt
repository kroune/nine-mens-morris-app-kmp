package io.github.kroune.nine_mens_morris_kmp_app.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.kroune.nine_mens_morris_kmp_app.navigation.AnimateAbleConfiguration
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun ComponentContext.componentCoroutineScope(): CoroutineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }
    return scope
}

fun <T : AnimateAbleConfiguration> StackNavigation<T>.popWithFallback(
    customAnimation: StackAnimator = fade(),
    fallBackScreen: T
) {
    this.pop(animation = customAnimation) {
        if (!it) {
            this.replaceCurrent(fallBackScreen)
        }
    }
}