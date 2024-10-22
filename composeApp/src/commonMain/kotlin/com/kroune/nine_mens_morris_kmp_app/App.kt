package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import com.kroune.nine_mens_morris_kmp_app.screen.AppStartAnimationScreen
import com.kroune.nine_mens_morris_kmp_app.screen.WelcomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(component: RootComponent) {
    MaterialTheme {
        val childStack by component.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.AppStartAnimationScreenChild -> {
                    AppStartAnimationScreen(instance.component)
                }

                is RootComponent.Child.WelcomeScreenChild -> {
                    WelcomeScreen(instance.component)
                }
            }
        }
    }
}