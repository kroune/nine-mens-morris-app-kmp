package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.kroune.nine_mens_morris_kmp_app.common.collectValue
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Child
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Configuration
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
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.ViewOwnAccountScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.home.WelcomeScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme

@Composable
fun App(component: RootComponent) {
    val stackAnimation = stackAnimation<Configuration, Child> { it ->
        it.configuration.animation
    }
    AppTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            val childStack by component.childStack.subscribeAsState()
            Children(
                stack = childStack,
                animation = stackAnimation
            ) { child ->
                val instance = child.instance
                when (instance) {
                    is Child.AppStartAnimationScreenChild -> {
                        AppStartAnimationScreen(instance.component)
                    }

                    is Child.WelcomeScreenChild -> {
                        WelcomeScreen(instance.component)
                    }

                    is Child.ViewAccountScreenChild -> {
                        with(instance.component) {
                            ViewAccountScreen(
                                { onEvent(it) },
                                state.collectValue()
                            )
                        }
                    }

                    is Child.ViewOwnAccountScreenChild -> {
                        with(instance.component) {
                            ViewOwnAccountScreen({ onEvent(it) }, state.collectValue())
                        }
                    }

                    is Child.SignUpScreenChild -> {
                        SignUpScreen(
                            instance.component.state.collectValue(),
                            { instance.component.onEvent(it) }
                        )
                    }

                    is Child.SignInScreenChild -> {
                        SignInScreen(
                            instance.component.state.collectValue(),
                            { instance.component.onEvent(it) }
                        )
                    }

                    is Child.GameWithFriendChild -> {
                        GameWithFriendScreen(instance.component)
                    }

                    is Child.GameWithBotChild -> {
                        GameWithBotScreen(instance.component)
                    }

                    is Child.SearchingForGameChild -> {
                        SearchingForGameScreen(instance.component)
                    }

                    is Child.OnlineGameChild -> {
                        with(instance.component) {
                            OnlineGameScreen(
                                { onEvent(it) },
                                state.collectValue()
                            )
                        }
                    }

                    is Child.LeaderboardChild -> {
                        with(instance.component) {
                            LeaderboardScreen(
                                { onEvent(it) },
                                state.collectValue()
                            )
                        }
                    }
                }
                BackHandler(instance.component.backHandler) {
                    instance.component.onBackPressed()
                }
            }
        }
    }
}