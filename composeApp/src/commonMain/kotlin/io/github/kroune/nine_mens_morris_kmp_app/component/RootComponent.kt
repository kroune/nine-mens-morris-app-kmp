package io.github.kroune.nine_mens_morris_kmp_app.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.slide
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.aboutScreenComponent.AboutScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.appVersion.AppVersionRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.navigation.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Configuration
import io.github.kroune.nine_mens_morris_kmp_app.navigation.RootChild
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.invertedSlide
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(ExperimentalDecomposeApi::class)
class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, WebNavigationOwner, KoinComponent {
    private val appVersionRepository: AppVersionRepositoryI = get<AppVersionRepositoryI>()
    private val componentScope = componentCoroutineScope()

    private val navigation: StackNavigation<Configuration> = StackNavigation()

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


    private fun popWithRootFallback(
        customAnimation: StackAnimator = fade(),
        fallBackScreen: Configuration = Configuration.AppStartAnimation()
    ) {
        navigation.popWithFallback(customAnimation, fallBackScreen)
    }

    init {
        childStack.subscribe { childStack ->
            BackHandler.setCallbackAction {
                childStack.active.instance.component.onBackPressed()
            }
        }
    }

    private val _state = MutableStateFlow(
        RootScreenState(
            null,
            null
        )
    )
    val state
        get() = _state

    // version check
    init {
        with(componentScope) {
            launch {
                _state.update {
                    it.copy(
                        lastVersion = appVersionRepository.getLastAppVersion()
                    )
                }
            }
            launch {
                _state.update {
                    it.copy(
                        requiredVersion = appVersionRepository.getRequiredVersion()
                    )
                }
            }
        }
    }

    /**
     * Child factory
     */
    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ): RootChild {
        return when (config) {
            is Configuration.AppStartAnimation -> {
                RootChild.AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        componentContext = context,
                        onNavigationToWelcomeScreen = {
                            navigation.pushToFront(Configuration.WelcomeScreen())
                        }
                    )
                )
            }

            is Configuration.WelcomeScreen -> {
                RootChild.WelcomeScreenChild(
                    WelcomeScreenComponent(
                        componentContext = context,
                        onNavigationToGameWithFriendScreen = {
                            navigation.pushToFront(
                                Configuration.GameWithFriendScreen(scale() + fade())
                            )
                        },
                        onNavigationToGameWithBotScreen = {
                            navigation.pushToFront(
                                Configuration.GameWithBotScreen(scale() + fade())
                            )
                        },
                        onNavigationToOnlineGameScreen = {
                            navigation.pushToFront(
                                Configuration.SearchingForGameScreen(scale() + fade())
                            )
                        },
                        onNavigationToLeaderboardScreen = {
                            navigation.pushToFront(
                                Configuration.LeaderboardScreen(scale() + fade())
                            )
                        },
                        onNavigationToAuthScreen = {
                            navigation.pushToFront(
                                Configuration.SignUpScreen(invertedSlide())
                            )
                        },
                        onNavigationToAccountViewScreen = {
                            navigation.pushToFront(
                                Configuration.ViewOwnAccountScreen(
                                    accountId = it,
                                    customAnimation = invertedSlide(),
                                )
                            )
                        },
                        onNavigationToAboutScreen = {
                            navigation.pushToFront(
                                Configuration.AboutScreen()
                            )
                        },
                        onNavigationBack = {
                            navigation.pushToFront(
                                Configuration.AppStartAnimation()
                            )
                        },
                        accountIdRepository = get(),
                        jwtTokenRepository = get(),
                    )
                )
            }

            is Configuration.ViewOwnAccountScreen -> {
                RootChild.ViewOwnAccountScreenChild(
                    ViewOwnAccountScreenComponent(
                        onNavigationBack = { popWithRootFallback(config.customAnimation) },
                        accountId = config.accountId,
                        componentContext = context,
                        accountInfoRepository = get(),
                        jwtTokenInteractor = get()
                    )
                )
            }

            is Configuration.ViewAccountScreen -> {
                RootChild.ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        onNavigationBack = { popWithRootFallback(config.customAnimation) },
                        accountId = config.accountId,
                        componentContext = context,
                        jwtTokenInteractor = get(),
                    )
                )
            }

            is Configuration.SignUpScreen -> {
                RootChild.SignUpScreenChild(
                    SignUpScreenComponent(
                        onNavigationBack = { popWithRootFallback(invertedSlide()) },
                        onNavigationToSignInScreen = {
                            navigation.replaceCurrent(
                                Configuration.SignInScreen(customAnimation = invertedSlide())
                            )
                        },
                        onSuccessfulAuth = { popWithRootFallback(config.customAnimation) },
                        componentContext = context,
                        accountIdRepository = get(),
                        authRepository = get(),
                    )
                )
            }

            is Configuration.SignInScreen -> {
                RootChild.SignInScreenChild(
                    SignInScreenComponent(
                        onNavigationBack = { popWithRootFallback(config.customAnimation) },
                        onNavigationToSignUpScreen = {
                            navigation.replaceCurrent(
                                Configuration.SignUpScreen(customAnimation = invertedSlide())
                            )
                        },
                        onSuccessfulAuth = { popWithRootFallback(config.customAnimation) },
                        componentContext = context,
                        accountIdRepository = get(),
                        authRepository = get(),
                    )
                )
            }

            is Configuration.GameWithFriendScreen -> {
                RootChild.GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        onNavigationBack = { popWithRootFallback(config.customAnimation) },
                        componentContext = context
                    )
                )
            }

            is Configuration.GameWithBotScreen -> {
                RootChild.GameWithBotChild(
                    GameWithBotScreenComponent(
                        { popWithRootFallback(config.customAnimation) },
                        context,
                    )
                )
            }

            is Configuration.SearchingForGameScreen -> {
                RootChild.SearchingForGameChild(
                    SearchingForGameComponent(
                        onGameFind = { gameId ->
                            popWithRootFallback(config.customAnimation)
                            navigation.pushToFront(
                                Configuration.OnlineGameScreen(
                                    gameId,
                                    scale(),
                                )
                            )
                        },
                        onGoingToWelcomeScreen = { popWithRootFallback(config.customAnimation) },
                        searchingForGameRepository = get(),
                        context,
                    )
                )
            }

            is Configuration.OnlineGameScreen -> {
                RootChild.OnlineGameChild(
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
                        onlineGameRepository = get(),
                        accountIdRepository = get(),
                        componentContext = context,
                    )
                )
            }

            is Configuration.LeaderboardScreen -> {
                RootChild.LeaderboardChild(
                    LeaderboardScreenComponent(
                        {
                            navigation.pushToFront(
                                Configuration.ViewAccountScreen(it, fade())
                            )
                        },
                        {
                            popWithRootFallback(config.customAnimation)
                        },
                        accountInfoRepository = get(),
                        context,
                    )
                )
            }

            is Configuration.AboutScreen -> {
                RootChild.AboutChild(
                    AboutScreenComponent(
                        {
                            popWithRootFallback(slide())
                        },
                        context,
                    )
                )
            }
        }
    }
}

data class RootScreenState(
    val lastVersion: AppLastVersionApiResponse?,
    val requiredVersion: RequiredVersionApiResponse?
)
