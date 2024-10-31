package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kroune.nine_mens_morris_kmp_app.common.AppTheme
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import com.kroune.nine_mens_morris_kmp_app.screen.AppStartAnimationScreen
import com.kroune.nine_mens_morris_kmp_app.screen.GameWithBotScreen
import com.kroune.nine_mens_morris_kmp_app.screen.GameWithFriendScreen
import com.kroune.nine_mens_morris_kmp_app.screen.OnlineGameScreen
import com.kroune.nine_mens_morris_kmp_app.screen.SearchingForGameScreen
import com.kroune.nine_mens_morris_kmp_app.screen.SignInScreen
import com.kroune.nine_mens_morris_kmp_app.screen.SignUpScreen
import com.kroune.nine_mens_morris_kmp_app.screen.ViewAccountScreen
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
            AppTheme {
                when (val instance = child.instance) {
                    is RootComponent.Child.AppStartAnimationScreenChild -> {
                        AppStartAnimationScreen(instance.component)
                    }

                    is RootComponent.Child.WelcomeScreenChild -> {
                        WelcomeScreen(instance.component)
                    }

                    is RootComponent.Child.ViewAccountScreenChild -> {
                        ViewAccountScreen(instance.component)
                    }

                    is RootComponent.Child.SignUpScreenChild -> {
                        SignUpScreen(instance.component)
                    }

                    is RootComponent.Child.SignInScreenChild -> {
                        SignInScreen(instance.component)
                    }

                    is RootComponent.Child.GameWithFriendChild -> {
                        GameWithFriendScreen(instance.component)
                    }

                    is RootComponent.Child.GameWithBotChild -> {
                        GameWithBotScreen(instance.component)
                    }

                    is RootComponent.Child.SearchingForGameChild -> {
                        SearchingForGameScreen(instance.component)
                    }

                    is RootComponent.Child.OnlineGameChild -> {
                        OnlineGameScreen(instance.component)
                    }
                }
            }
        }
    }
}