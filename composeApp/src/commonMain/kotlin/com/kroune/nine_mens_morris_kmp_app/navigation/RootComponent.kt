package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.kroune.nine_mens_morris_kmp_app.component.AppStartAnimationComponent
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.component.LeaderboardComponent
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.auth.SignInScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.auth.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = AppStartAnimation,
        handleBackButton = false,
        childFactory = ::createChild
    )

    init {
        // TODO: fix this absolute garbage
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                BackHandler.setCallbackAction {
                    childStack.active.instance.component.onBackPressed()
                }
                delay(500L)
            }
        }
    }
    private fun popOrReplaceWithWelcomeScreenIfStackIsEmpty() {
        navigation.pop {
            if (!it) {
                navigation.replaceCurrent(WelcomeScreen)
            }
        }
    }

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
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
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
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                        },
                        {
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                            navigation.pushToFront(SignInScreen(it))
                        },
                        { it: Configuration ->
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
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
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                        },
                        {
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                            navigation.pushToFront(SignUpScreen(it))
                        },
                        { it: Configuration ->
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
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
                        {
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                        },
                        context
                    )
                )
            }

            GameWithBotScreen -> {
                GameWithBotChild(
                    GameWithBotScreenComponent(
                        {
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                        },
                        context
                    )
                )
            }

            SearchingForGameScreen -> {
                SearchingForGameChild(
                    SearchingForGameComponent(
                        { gameId ->
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                            navigation.pushToFront(OnlineGameScreen(gameId))
                        },
                        {
                            // we don't save state
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                            navigation.pushToFront(WelcomeScreen)
                        },
                        context
                    )
                )
            }

            is OnlineGameScreen -> {
                OnlineGameChild(
                    OnlineGameComponent(
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
                        {
                            popOrReplaceWithWelcomeScreenIfStackIsEmpty()
                        },
                        context
                    )
                )
            }
        }
    }

    sealed class Child(open val component: ComponentContextWithBackHandle) {
        data class AppStartAnimationScreenChild(override val component: AppStartAnimationComponent) :
            Child(component)

        data class WelcomeScreenChild(override val component: WelcomeScreenComponent) :
            Child(component)

        data class ViewAccountScreenChild(override val component: ViewAccountScreenComponent) :
            Child(component)

        data class SignUpScreenChild(override val component: SignUpScreenComponent) :
            Child(component)

        data class SignInScreenChild(override val component: SignInScreenComponent) :
            Child(component)

        data class GameWithFriendChild(override val component: GameWithFriendScreenComponent) :
            Child(component)

        data class GameWithBotChild(override val component: GameWithBotScreenComponent) :
            Child(component)

        data class SearchingForGameChild(override val component: SearchingForGameComponent) :
            Child(component)

        data class OnlineGameChild(override val component: OnlineGameComponent) : Child(component)
        data class LeaderboardChild(override val component: LeaderboardComponent) : Child(component)
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