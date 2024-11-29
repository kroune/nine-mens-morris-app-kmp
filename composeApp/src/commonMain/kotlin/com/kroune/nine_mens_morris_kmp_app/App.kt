package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kroune.nine_mens_morris_kmp_app.common.AppTheme
import com.kroune.nine_mens_morris_kmp_app.common.BackHandler
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent
import com.kroune.nine_mens_morris_kmp_app.screen.AppStartAnimationScreen
import com.kroune.nine_mens_morris_kmp_app.screen.LeaderboardScreen
import com.kroune.nine_mens_morris_kmp_app.screen.ViewAccountScreen
import com.kroune.nine_mens_morris_kmp_app.screen.WelcomeScreen
import com.kroune.nine_mens_morris_kmp_app.screen.auth.SignInScreen
import com.kroune.nine_mens_morris_kmp_app.screen.auth.SignUpScreen
import com.kroune.nine_mens_morris_kmp_app.screen.game.GameWithBotScreen
import com.kroune.nine_mens_morris_kmp_app.screen.game.GameWithFriendScreen
import com.kroune.nine_mens_morris_kmp_app.screen.game.OnlineGameScreen
import com.kroune.nine_mens_morris_kmp_app.screen.game.SearchingForGameScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun App(component: RootComponent) {
    MaterialTheme {
        val childStack by component.childStack.subscribeAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(slide())
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
            val instance = child.instance
            val requester = remember { FocusRequester() }
            Box(
                modifier = Modifier
                    .onKeyEvent {
                        if (it.key == Key.Escape) {
                            instance.component.onBackPressed()
                            return@onKeyEvent true
                        }
                        false
                    }
                    .focusRequester(requester)
                    .focusable()
            )
            // TODO: find cleaner way
            LaunchedEffect(Unit) {
                while (true) {
                    withContext(Dispatchers.Main) {
                        requester.requestFocus()
                    }
                    delay(500L)
                }
            }
        }
    }
}