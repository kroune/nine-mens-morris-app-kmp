package io.github.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import io.github.kroune.nine_mens_morris_kmp_app.common.customSlide
import io.github.kroune.nine_mens_morris_kmp_app.common.pop
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalDecomposeApi::class)
class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, WebNavigationOwner {

    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = getInitialConfiguration(),
        handleBackButton = false,
        childFactory = ::createChild
    )

    private fun getInitialConfiguration(): Configuration {
        return Configuration.AppStartAnimation(scale())
    }

    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = navigation,
            stack = childStack,
            serializer = Configuration.serializer(),
            parametersMapper = {
                buildMap {
                    put("url", it.configuration.urlName)
                }
            }
        )

    private fun popOrFallbackScreen(
        customAnimation: StackAnimator,
        fallBackScreen: Configuration = Configuration.WelcomeScreen(customAnimation)
    ) {
        navigation.pop(animation = customAnimation) {
            if (!it) {
                navigation.replaceCurrent(fallBackScreen)
            }
        }
    }

    init {
        // TODO: fix this absolute garbage
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                BackHandler.setCallbackAction {
                    childStack.active.instance.component.onBackPressed()
                }
                delay(300L)
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
        return when (config) {
            is Configuration.AppStartAnimation -> {
                Child.AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        componentContext = context,
                        onNavigationToWelcomeScreen = {
                            navigation.pushToFront(Configuration.WelcomeScreen())
                        }
                    )
                )
            }

            is Configuration.WelcomeScreen -> {
                Child.WelcomeScreenChild(
                    WelcomeScreenComponent(
                        componentContext = context,
                        onNavigationToGameWithFriendScreen = {
                            navigation.pushToFront(Configuration.GameWithFriendScreen(scale() + fade()))
                        },
                        onNavigationToGameWithBotScreen = {
                            navigation.pushToFront(Configuration.GameWithBotScreen(scale() + fade()))
                        },
                        onNavigationToOnlineGameScreen = {
                            navigation.pushToFront(Configuration.SearchingForGameScreen(scale() + fade()))
                        },
                        onNavigationToLeaderboardScreen = {
                            navigation.pushToFront(Configuration.LeaderboardScreen(scale() + fade()))
                        },
                        onNavigationToAuthScreen = {
                            navigation.pushToFront(
                                Configuration.SignUpScreen(
                                    customSlide(
                                        invertDirection = true
                                    )
                                )
                            )
                        },
                        onNavigationToAccountViewScreen = {
                            navigation.pushToFront(
                                Configuration.ViewOwnAccountScreen(
                                    accountId = it,
                                    customAnimation = customSlide(invertDirection = true),
                                )
                            )
                        },
                        onNavigationBack = {
                            navigation.pushToFront(
                                Configuration.AppStartAnimation()
                            )
                        }
                    )
                )
            }

            is Configuration.ViewOwnAccountScreen -> {
                Child.ViewOwnAccountScreenChild(
                    ViewOwnAccountScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(config.animation)
                        },
                        accountId = config.accountId,
                        componentContext = context
                    )
                )
            }

            is Configuration.ViewAccountScreen -> {
                Child.ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(config.animation)
                        },
                        accountId = config.accountId,
                        componentContext = context
                    )
                )
            }

            is Configuration.SignUpScreen -> {
                Child.SignUpScreenChild(
                    SignUpScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(
                                customSlide(invertDirection = true)
                            )
                        },
                        onNavigationToSignInScreen = {
                            navigation.replaceCurrent(
                                Configuration.SignInScreen(
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        onSuccessfulAuth = {
                            popOrFallbackScreen(config.animation)
                        },
                        componentContext = context
                    )
                )
            }

            is Configuration.SignInScreen -> {
                Child.SignInScreenChild(
                    SignInScreenComponent(
                        onNavigationBack = {
                            popOrFallbackScreen(config.animation)
                        },
                        onNavigationToSignUpScreen = {
                            navigation.replaceCurrent(
                                Configuration.SignUpScreen(
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        onSuccessfulAuth = {
                            popOrFallbackScreen(config.animation)
                        },
                        componentContext = context
                    )
                )
            }

            is Configuration.GameWithFriendScreen -> {
                Child.GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }

            is Configuration.GameWithBotScreen -> {
                Child.GameWithBotChild(
                    GameWithBotScreenComponent(
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }

            is Configuration.SearchingForGameScreen -> {
                Child.SearchingForGameChild(
                    SearchingForGameComponent(
                        onGameFind = { gameId ->
                            popOrFallbackScreen(config.animation)
                            navigation.pushToFront(
                                Configuration.OnlineGameScreen(
                                    gameId,
                                    scale()
                                )
                            )
                        },
                        onGoingToWelcomeScreen = {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }

            is Configuration.OnlineGameScreen -> {
                Child.OnlineGameChild(
                    OnlineGameComponent(
                        onNavigationToViewAccountScreen = {
                            navigation.pushToFront(Configuration.ViewAccountScreen(it))
                        },
                        onNavigationToViewOwnAccountScreen = {
                            navigation.pushToFront(Configuration.ViewOwnAccountScreen(it))
                        },
                        gameId = config.gameId,
                        onNavigationToWelcomeScreen = {
                            navigation.pushToFront(Configuration.WelcomeScreen())
                        },
                        componentContext = context
                    )
                )
            }

            is Configuration.LeaderboardScreen -> {
                Child.LeaderboardChild(
                    LeaderboardScreenComponent(
                        {
                            navigation.pushToFront(Configuration.ViewAccountScreen(it, scale()))
                        },
                        {
                            popOrFallbackScreen(config.animation)
                        },
                        context
                    )
                )
            }
        }
    }
}