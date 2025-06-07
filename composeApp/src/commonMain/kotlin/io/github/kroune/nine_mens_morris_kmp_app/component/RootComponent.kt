package io.github.kroune.nine_mens_morris_kmp_app.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
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
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Child
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Configuration
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.customSlide
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(ExperimentalDecomposeApi::class)
class RootComponent(
    @Suppress("LocalVariableName")
    _appVersionRepository: AppVersionRepositoryI? = null,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, WebNavigationOwner, KoinComponent {
    private val appVersionRepository: AppVersionRepositoryI =
        _appVersionRepository ?: get<AppVersionRepositoryI>()
    private val componentScope = componentCoroutineScope()

    private val navigation: StackNavigation<Configuration> = StackNavigation<Configuration>()

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
                        jwtTokenInteractor = get(),
                    )
                )
            }

            is Configuration.ViewOwnAccountScreen -> {
                Child.ViewOwnAccountScreenChild(
                    ViewOwnAccountScreenComponent(
                        onNavigationBack = {
                            popWithRootFallback(config.customAnimation)
                        },
                        accountId = config.accountId,
                        componentContext = context,
                        accountInfoRepository = get(),
                        jwtTokenInteractor = get()
                    )
                )
            }

            is Configuration.ViewAccountScreen -> {
                Child.ViewAccountScreenChild(
                    ViewAccountScreenComponent(
                        onNavigationBack = {
                            popWithRootFallback(config.customAnimation)
                        },
                        accountId = config.accountId,
                        componentContext = context,
                        jwtTokenInteractor = get(),
                    )
                )
            }

            is Configuration.SignUpScreen -> {
                Child.SignUpScreenChild(
                    SignUpScreenComponent(
                        onNavigationBack = {
                            popWithRootFallback(
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
                            popWithRootFallback(config.customAnimation)
                        },
                        componentContext = context,
                        accountIdRepository = get(),
                        authRepository = get(),
                    )
                )
            }

            is Configuration.SignInScreen -> {
                Child.SignInScreenChild(
                    SignInScreenComponent(
                        onNavigationBack = {
                            popWithRootFallback(config.customAnimation)
                        },
                        onNavigationToSignUpScreen = {
                            navigation.replaceCurrent(
                                Configuration.SignUpScreen(
                                    customAnimation = customSlide(invertDirection = true)
                                )
                            )
                        },
                        onSuccessfulAuth = {
                            popWithRootFallback(config.customAnimation)
                        },
                        componentContext = context,
                        accountIdRepository = get(),
                        authRepository = get(),
                    )
                )
            }

            is Configuration.GameWithFriendScreen -> {
                Child.GameWithFriendChild(
                    GameWithFriendScreenComponent(
                        {
                            popWithRootFallback(config.customAnimation)
                        },
                        context
                    )
                )
            }

            is Configuration.GameWithBotScreen -> {
                Child.GameWithBotChild(
                    GameWithBotScreenComponent(
                        {
                            popWithRootFallback(config.customAnimation)
                        },
                        context
                    )
                )
            }

            is Configuration.SearchingForGameScreen -> {
                Child.SearchingForGameChild(
                    SearchingForGameComponent(
                        onGameFind = { gameId ->
                            popWithRootFallback(config.customAnimation)
                            navigation.pushToFront(
                                Configuration.OnlineGameScreen(
                                    gameId,
                                    scale()
                                )
                            )
                        },
                        onGoingToWelcomeScreen = {
                            popWithRootFallback(config.customAnimation)
                        },
                        searchingForGameRepository = get(),
                        context,
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
                        onlineGameRepository = get(),
                        accountIdRepository = get(),
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
                            popWithRootFallback(config.customAnimation)
                        },
                        accountInfoRepository = get(),
                        context
                    )
                )
            }

            is Configuration.AboutScreen -> {
                Child.AboutChild(
                    AboutScreenComponent(
                        {
                            popWithRootFallback(customSlide(invertDirection = true))
                        },
                        context
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
