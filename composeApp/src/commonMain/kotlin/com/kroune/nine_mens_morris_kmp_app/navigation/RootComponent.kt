package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushToFront
import com.kroune.nine_mens_morris_kmp_app.component.AppStartAnimationComponent
import com.kroune.nine_mens_morris_kmp_app.component.LeaderboardComponent
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.auth.SignInScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.auth.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.AppStartAnimationScreenChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.GameWithBotChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.GameWithFriendChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.OnlineGameChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.SearchingForGameChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.SignInScreenChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.SignUpScreenChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.ViewAccountScreenChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.WelcomeScreenChild
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.AppStartAnimation
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.GameWithBotScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.GameWithFriendScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.OnlineGameScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.SearchingForGameScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.SignInScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.SignUpScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.ViewAccountScreen
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.WelcomeScreen
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = AppStartAnimation,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is AppStartAnimation -> {
                AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        context,
                        {
                            navigation.pushToFront(WelcomeScreen)
                        }
                    )
                )
            }

            is WelcomeScreen -> {
                WelcomeScreenChild(
                    WelcomeScreenComponent(
                        context,
                        {
                            navigation.pushToFront(GameWithFriendScreen)
                        },
                        {
                            navigation.pushToFront(GameWithBotScreen)
                        },
                        {
                            navigation.pushToFront(SearchingForGameScreen)
                        },
                        {
                            navigation.pushToFront(Configuration.LeaderboardScreen)
                        },
                        {
                            navigation.pushToFront(SignUpScreen { accountId ->
                                ViewAccountScreen(true, accountId)
                            })
                        },
                        {
                            navigation.pushToFront(SignUpScreen { _ ->
                                SearchingForGameScreen
                            })
                        },
                        {
                            navigation.pushToFront(SignUpScreen { _ ->
                                Configuration.LeaderboardScreen
                            })
                        },
                        { accountId ->
                            navigation.pushToFront(ViewAccountScreen(true, accountId))
                        },
                        {
                            navigation.pushToFront(AppStartAnimation)
                        }
                    )
                )
            }

            is ViewAccountScreen -> {
                ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        {
                            navigation.popWhile { it != WelcomeScreen }
                        },
                        config.isOwnAccount,
                        config.accountId,
                        context
                    )
                )
            }

            is SignUpScreen -> {
                SignUpScreenChild(
                    SignUpScreenComponent(
                        {
                            navigation.pushToFront(SignInScreen(it))
                        },
                        { it: Configuration ->
                            navigation.pushToFront(it)
                        },
                        config.nextScreen,
                        context
                    )
                )
            }

            is SignInScreen -> {
                SignInScreenChild(
                    SignInScreenComponent(
                        {
                            navigation.pushToFront(SignUpScreen(it))
                        },
                        { it: Configuration ->
                            navigation.pushToFront(it)
                        },
                        config.nextScreen,
                        context
                    )
                )
            }

            GameWithFriendScreen -> {
                GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        context
                    )
                )
            }

            GameWithBotScreen -> {
                GameWithBotChild(
                    GameWithBotScreenComponent(
                        context
                    )
                )
            }

            SearchingForGameScreen -> {
                SearchingForGameChild(
                    SearchingForGameScreenComponent(
                        { gameId ->
                            navigation.pop()
                            navigation.pushToFront(OnlineGameScreen(gameId))
                        },
                        {
                            navigation.pushToFront(WelcomeScreen)
                        },
                        context
                    )
                )
            }

            is OnlineGameScreen -> {
                OnlineGameChild(
                    OnlineGameScreenComponent(
                        config.gameId,
                        {
                            navigation.pushToFront(WelcomeScreen)
                        },
                        context
                    )
                )
            }

            Configuration.LeaderboardScreen -> {
                Child.LeaderboardChild(
                    LeaderboardComponent(
                        context
                    )
                )
            }
        }
    }

    sealed class Child {
        data class AppStartAnimationScreenChild(val component: AppStartAnimationComponent) : Child()
        data class WelcomeScreenChild(val component: WelcomeScreenComponent) : Child()
        data class ViewAccountScreenChild(val component: ViewAccountScreenComponent) : Child()
        data class SignUpScreenChild(val component: SignUpScreenComponent) : Child()
        data class SignInScreenChild(val component: SignInScreenComponent) : Child()
        data class GameWithFriendChild(val component: GameWithFriendScreenComponent) : Child()
        data class GameWithBotChild(val component: GameWithBotScreenComponent) : Child()
        data class SearchingForGameChild(val component: SearchingForGameScreenComponent) : Child()
        data class OnlineGameChild(val component: OnlineGameScreenComponent) : Child()
        data class LeaderboardChild(val component: LeaderboardComponent) : Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object AppStartAnimation : Configuration()

        @Serializable
        data object WelcomeScreen : Configuration()

        @Serializable
        data class ViewAccountScreen(val isOwnAccount: Boolean, val accountId: Long) :
            Configuration()

        @Serializable
        data class SignUpScreen(val nextScreen: (Long) -> Configuration) : Configuration()

        @Serializable
        data class SignInScreen(val nextScreen: (Long) -> Configuration) : Configuration()

        @Serializable
        data object GameWithFriendScreen : Configuration()

        @Serializable
        data object GameWithBotScreen : Configuration()

        @Serializable
        data object SearchingForGameScreen : Configuration()

        @Serializable
        data class OnlineGameScreen(val gameId: Long) : Configuration()

        @Serializable
        data object LeaderboardScreen : Configuration()
    }
}