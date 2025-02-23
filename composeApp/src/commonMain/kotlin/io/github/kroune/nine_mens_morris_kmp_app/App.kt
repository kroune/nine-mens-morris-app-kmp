package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.FaultyDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.kroune.nine_mens_morris_kmp_app.common.AppTheme
import io.github.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.screen.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.SignInScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.SignUpScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithBotScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithFriendScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.OnlineGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.SearchingForGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.LeaderboardScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.ViewAccountScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.WelcomeScreen


@OptIn(FaultyDecomposeApi::class)
@Composable
fun App(component: RootComponent) {
    val stackAnimation =
        stackAnimation<RootComponent.Configuration, RootComponent.Child> { child, otherChild, direction ->
            when (direction) {
                Direction.EXIT_BACK -> {
                    otherChild.configuration.animation
                }

                Direction.ENTER_FRONT -> {
                    child.configuration.animation
                }

                Direction.EXIT_FRONT -> {
                    otherChild.configuration.animation
                }

                Direction.ENTER_BACK -> {
                    child.configuration.animation
                }
            }
        }
    MaterialTheme {
        val childStack by component.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation
        ) { child ->
            AppTheme {
                val instance = child.instance
                when (instance) {
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

                    is RootComponent.Child.LeaderboardChild -> {
                        LeaderboardScreen(instance.component)
                    }
                }
                BackHandler(instance.component.backHandler) {
                    instance.component.onBackPressed()
                }
            }
        }
    }
}