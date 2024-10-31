package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.kroune.nine_mens_morris_kmp_app.component.AppStartAnimationComponent
import com.kroune.nine_mens_morris_kmp_app.component.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.OnlineGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.SearchingForGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.SignInScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.*
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.*
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration.SearchingForGameScreen
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
                            navigation.pushNew(WelcomeScreen)
                        }
                    )
                )
            }

            is WelcomeScreen -> {
                WelcomeScreenChild(
                    WelcomeScreenComponent(
                        context,
                        {
                            navigation.pushNew(GameWithFriendScreen)
                        },
                        {
                            navigation.pushNew(GameWithBotScreen)
                        },
                        {
                            navigation.pushNew(SearchingForGameScreen)
                        },
                        {
                            navigation.pushNew(SignUpScreen { accountId ->
                                ViewAccountScreen(true, accountId)
                            })
                        },
                        {
                            navigation.pushNew(SignUpScreen { accountId ->
                                SearchingForGameScreen
                            })
                        },
                        { accountId ->
                            navigation.pushNew(ViewAccountScreen(true, accountId))
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
                            navigation.pushNew(it)
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
                            navigation.pushNew(it)
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
                            navigation.pushNew(OnlineGameScreen(gameId))
                        },
                        context
                    )
                )
            }

            is OnlineGameScreen -> {
                OnlineGameChild(
                    OnlineGameScreenComponent(
                        config.gameId,
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
        data class OnlineGameChild(val component: OnlineGameScreenComponent): Child()
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
        data class OnlineGameScreen(val gameId: Long): Configuration()
    }
}