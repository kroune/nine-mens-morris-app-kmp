package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.kroune.nine_mens_morris_kmp_app.common.customSlide
import com.kroune.nine_mens_morris_kmp_app.common.pop
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.component.auth.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import com.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import com.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardComponent
import com.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.other.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponent
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
        initialConfiguration = AppStartAnimation(scale()),
        handleBackButton = false,
        childFactory = ::createChild
    )

    private fun popOrFallbackScreen(
        customAnimation: StackAnimator,
        fallBackScreen: Configuration = WelcomeScreen(customAnimation)
    ) {
        navigation.pop(animation = customAnimation) {
            if (!it) {
                navigation.replaceCurrent(fallBackScreen)
            }
        }
    }

    /**
     * Child factory
     */
    fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        BackHandler.setCallbackAction {
            childStack.active.instance.component.onBackPressed()
        }
        return when (config) {
            is AppStartAnimation -> {
                AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        componentContext = context,
                        onNavigationToWelcomeScreen = {
                            navigation.pushToFront(WelcomeScreen(scale()))
                        }
                    )
                )
            }

            is WelcomeScreen -> {
                WelcomeScreenChild(
                    WelcomeScreenComponent(
                        componentContext = context,
                        onNavigationToGameWithFriendScreen = {
                            navigation.pushToFront(GameWithFriendScreen(scale()))
                        },
                        onNavigationToGameWithBotScreen = {
                            navigation.pushToFront(GameWithBotScreen(scale()))
                        },
                        onNavigationToOnlineGameScreen = {
                            navigation.pushToFront(SearchingForGameScreen(scale()))
                        },
                        onNavigationToLeaderboardScreen = {
                            navigation.pushToFront(Configuration.LeaderboardScreen(scale()))
                        },
                        onNavigationToAccountRegistrationThenViewAccountScreen = {
                            navigation.pushToFront(
                                SignUpScreen(
                                    nextScreen = { accountId ->
                                        ViewAccountScreen(
                                            isOwnAccount = true,
                                            accountId = accountId,
                                            customAnimation = customSlide(invertDirection = true)
                                        )
                                    },
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        onNavigationToAccountRegistrationThenOnlineGameScreen = {
                            navigation.pushToFront(
                                SignUpScreen(
                                    nextScreen = { _ ->
                                        SearchingForGameScreen(scale())
                                    },
                                    customAnimation = scale()
                                )
                            )
                        },
                        onNavigationToAccountRegistrationThenLeaderboardScreen = {
                            navigation.pushToFront(
                                SignUpScreen(
                                    nextScreen = { _ ->
                                        Configuration.LeaderboardScreen(scale())
                                    },
                                    customAnimation = scale()
                                )
                            )
                        },
                        onNavigationToAccountViewScreen = { accountId ->
                            navigation.pushToFront(
                                ViewAccountScreen(
                                    isOwnAccount = true,
                                    accountId = accountId,
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        onNavigationToAppStartAnimationScreen = {
                            navigation.pushToFront(
                                AppStartAnimation(
                                    customAnimation = scale()
                                )
                            )
                        }
                    )
                )
            }

            is ViewAccountScreen -> {
                ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(config.animation)
                        },
                        isOwnAccount = config.isOwnAccount,
                        accountId = config.accountId,
                        componentContext = context
                    )
                )
            }

            is SignUpScreen -> {
                SignUpScreenChild(
                    SignUpScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(
                                customSlide(invertDirection = true)
                            )
                        },
                        onNavigationToSignInScreen = {
                            navigation.replaceCurrent(
                                SignInScreen(
                                    nextScreen = it,
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        switchingScreensLambda = { it: Configuration ->
                            navigation.replaceCurrent(it)
                        },
                        nextScreen = config.nextScreen,
                        componentContext = context
                    )
                )
            }

            is SignInScreen -> {
                SignInScreenChild(
                    SignInScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(config.animation)
                        },
                        onNavigationToSignUpScreen = {
                            navigation.replaceCurrent(
                                SignUpScreen(
                                    nextScreen = it,
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        switchingScreensLambda = { it: Configuration ->
                            navigation.replaceCurrent(it)
                        },
                        nextScreen = config.nextScreen,
                        componentContext = context
                    )
                )
            }

            is GameWithFriendScreen -> {
                GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }

            is GameWithBotScreen -> {
                GameWithBotChild(
                    GameWithBotScreenComponent(
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }

            is SearchingForGameScreen -> {
                SearchingForGameChild(
                    SearchingForGameComponent(
                        onGameFind = { gameId ->
                            popOrFallbackScreen(config.animation)
                            navigation.pushToFront(
                                OnlineGameScreen(
                                    gameId,
                                    scale()
                                )
                            )
                        },
                        onGoingToWelcomeScreen = {
                            // we don't save state
                            popOrFallbackScreen(config.animation)
                            navigation.pushToFront(
                                WelcomeScreen(
                                    scale()
                                )
                            )
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
                            navigation.pushToFront(WelcomeScreen(scale()))
                        },
                        context
                    )
                )
            }

            is Configuration.LeaderboardScreen -> {
                Child.LeaderboardChild(
                    LeaderboardComponent(
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }
        }
    }

    sealed class Child(open val component: ComponentContextWithBackHandle) {
        data class AppStartAnimationScreenChild(
            override val component: AppStartAnimationComponent
        ) : Child(component)

        data class WelcomeScreenChild(
            override val component: WelcomeScreenComponent
        ) : Child(component)

        data class ViewAccountScreenChild(
            override val component: ViewAccountScreenComponent
        ) : Child(component)

        data class SignUpScreenChild(
            override val component: SignUpScreenComponent
        ) : Child(component)

        data class SignInScreenChild(
            override val component: SignInScreenComponent
        ) : Child(component)

        data class GameWithFriendChild(
            override val component: GameWithFriendScreenComponent
        ) : Child(component)

        data class GameWithBotChild(
            override val component: GameWithBotScreenComponent
        ) : Child(component)

        data class SearchingForGameChild(
            override val component: SearchingForGameComponent
        ) : Child(component)

        data class OnlineGameChild(
            override val component: OnlineGameComponent
        ) : Child(component)

        data class LeaderboardChild(
            override val component: LeaderboardComponent
        ) : Child(component)
    }

    @Serializable
    sealed class Configuration(var animation: StackAnimator = slide()) {
        @Serializable
        data class AppStartAnimation(
            val customAnimation: StackAnimator
        ) : Configuration(scale())

        @Serializable
        data class WelcomeScreen(
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class ViewAccountScreen(
            val isOwnAccount: Boolean,
            val accountId: Long,
            val customAnimation: StackAnimator
        ) :
            Configuration(customAnimation)

        @Serializable
        data class SignUpScreen(
            val nextScreen: (Long) -> Configuration,
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class SignInScreen(
            val nextScreen: (Long) -> Configuration,
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class GameWithFriendScreen(
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class GameWithBotScreen(
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class SearchingForGameScreen(
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class OnlineGameScreen(
            val gameId: Long,
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)

        @Serializable
        data class LeaderboardScreen(
            val customAnimation: StackAnimator
        ) : Configuration(customAnimation)
    }
}