package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.kroune.nine_mens_morris_kmp_app.component.AppStartAnimationComponent
import com.kroune.nine_mens_morris_kmp_app.component.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.SignInScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Child.*
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.AppStartAnimation,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Configuration.AppStartAnimation -> {
                AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        context,
                        {
                            navigation.pushNew(Configuration.WelcomeScreen)
                        }
                    )
                )
            }

            is Configuration.WelcomeScreen -> {
                WelcomeScreenChild(
                    WelcomeScreenComponent(
                        context,
                        {
                            navigation.pushNew(Configuration.GameWithFriendScreen)
                        },
                        {
//                            navigation.pushNew(Configuration.GameWithBotScreen)
                        },
                        {
//                            navigation.pushNew(Configuration.OnlineGameScreen)
                        },
                        {
                            navigation.pushNew(Configuration.SignUpScreen() { accountId ->
                                Configuration.ViewAccountScreen(true, accountId)
                            })
                        },
                        { accountId ->
                            navigation.pushNew(Configuration.ViewAccountScreen(true, accountId))
                        }
                    )
                )
            }

            is Configuration.ViewAccountScreen -> {
                ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        {
                            navigation.popWhile { it != Configuration.WelcomeScreen }
                        },
                        config.isOwnAccount,
                        config.accountId,
                        context
                    )
                )
            }

            is Configuration.SignUpScreen -> {
                SignUpScreenChild(
                    SignUpScreenComponent(
                        {
                            navigation.pushToFront(Configuration.SignInScreen(it))
                        },
                        { it: Configuration ->
                            navigation.pushNew(it)
                        },
                        config.nextScreen,
                        context
                    )
                )
            }

            is Configuration.SignInScreen -> {
                SignInScreenChild(
                    SignInScreenComponent(
                        {
                            navigation.pushToFront(Configuration.SignUpScreen(it))
                        },
                        { it: Configuration ->
                            navigation.pushNew(it)
                        },
                        config.nextScreen,
                        context
                    )
                )
            }

            Configuration.GameWithFriendScreen -> {
                GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        {
                            TODO()
                        },
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
    }
}