package io.github.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import io.github.kroune.nine_mens_morris_kmp_app.BuildKonfig
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Configuration
import io.github.kroune.nine_mens_morris_kmp_app.navigation.RootChild
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signInScreen.SignInScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signUpScreen.SignUpScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.BackHandler
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.collectValue
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithBotScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithFriendScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.onlineGameScreen.OnlineGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.searchingForGameScreen.SearchingForGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AboutScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.LeaderboardScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.ViewAccountScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.viewOwnAccountScreen.ViewOwnAccountScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen.WelcomeScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.new_update
import ninemensmorrisappkmp.composeapp.generated.resources.required_update
import ninemensmorrisappkmp.composeapp.generated.resources.update
import org.jetbrains.compose.resources.stringResource

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalDecomposeApi::class
)
@Composable
fun RootScreen(component: RootComponent) {
    val stackAnimation = stackAnimation<Configuration, RootChild>(
        selector = { child,
                     otherChild,
                     direction,
                     isPredictiveBack ->
            when (direction) {
                Direction.EXIT_BACK -> {
                    otherChild.configuration.customAnimation
                }

                Direction.ENTER_FRONT -> {
                    child.configuration.customAnimation
                }

                Direction.EXIT_FRONT -> {
                    otherChild.configuration.customAnimation
                }

                Direction.ENTER_BACK -> {
                    child.configuration.customAnimation
                }
            }
        }
    )
    AppTheme {
        Scaffold {
            Box(
                modifier = Modifier.padding(it)
            ) {
                val childStack: ChildStack<Configuration, RootChild> =
                    component.childStack.subscribeAsState().value
                SharedTransitionLayout {
                    com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack<Configuration, RootChild>(
                        stack = childStack,
                        modifier = Modifier,
                        animation = stackAnimation,
                    ) { child ->
                        val instance = child.instance
                        when (instance) {
                            is RootChild.AppStartAnimationScreenChild -> {
                                with(instance.component) {
                                    AppStartAnimationScreen(
                                        onEvent = { onEvent(it) }
                                    )
                                }
                            }

                            is RootChild.WelcomeScreenChild -> {
                                with(instance.component) {
                                    WelcomeScreen(
                                        onEvent = { onEvent(it) },
                                        state = state.collectValue()
                                    )
                                }
                            }

                            is RootChild.ViewAccountScreenChild -> {
                                with(instance.component) {
                                    ViewAccountScreen(
                                        onEvent = { onEvent(it) },
                                        state = state.collectValue(),
                                        this@SharedTransitionLayout,
                                        this@ChildStack
                                    )
                                }
                            }

                            is RootChild.ViewOwnAccountScreenChild -> {
                                with(instance.component) {
                                    ViewOwnAccountScreen(
                                        onEvent = { onEvent(it) },
                                        state = state.collectValue()
                                    )
                                }
                            }

                            is RootChild.SignUpScreenChild -> {
                                with(instance.component) {
                                    SignUpScreen(
                                        component = state.collectValue(),
                                        onEvent = { onEvent(it) }
                                    )
                                }
                            }

                            is RootChild.SignInScreenChild -> {
                                with(instance.component) {
                                    SignInScreen(
                                        state = state.collectValue(),
                                        onEvent = { onEvent(it) }
                                    )
                                }
                            }

                            is RootChild.GameWithFriendChild -> {
                                with(instance.component) {
                                    GameWithFriendScreen(
                                        state = state.collectValue(),
                                        onEvent = { onEvent(it) }
                                    )
                                }
                            }

                            is RootChild.GameWithBotChild -> {
                                with(instance.component) {
                                    GameWithBotScreen(
                                        state = state.collectValue(),
                                        onEvent = { onEvent(it) }
                                    )
                                }
                            }

                            is RootChild.SearchingForGameChild -> {
                                SearchingForGameScreen(instance.component)
                            }

                            is RootChild.OnlineGameChild -> {
                                with(instance.component) {
                                    OnlineGameScreen(
                                        { onEvent(it) },
                                        state.collectValue()
                                    )
                                }
                            }

                            is RootChild.LeaderboardChild -> {
                                with(instance.component) {
                                    LeaderboardScreen(
                                        { onEvent(it) },
                                        state.collectValue(),
                                        this@SharedTransitionLayout,
                                        this@ChildStack
                                    )
                                }
                            }

                            is RootChild.AboutChild -> {
                                with(instance.component) {
                                    AboutScreen(
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
                    val state = component.state.collectValue()
                    with(state) {
                        DrawUpdateDialog(
                            lastVersion,
                            requiredVersion
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawUpdateDialog(
    lastVersion: AppLastVersionApiResponse?,
    requiredVersion: RequiredVersionApiResponse?,
) {
    val lastVersion = lastVersion.let {
        if (it !is AppLastVersionApiResponse.Success)
            return
        if (it.lastVersion == null)
            return
        if (it.lastVersion <= BuildKonfig.versionInt)
            return
        it.lastVersion
    }
    val hasToUpdate = requiredVersion.let {
        if (it !is RequiredVersionApiResponse.Success)
            return
        it.requiredVersion
    }
    var shouldAlert by rememberSaveable { mutableStateOf(true) }
    if (shouldAlert)
        BasicAlertDialog(
            {
                shouldAlert = false
            }
        ) {
            Surface(
                shape = UiConstants.RoundedCornerShape3
            ) {
                Column(
                    modifier = Modifier
                        .padding(UiConstants.padding3)
                        .width(IntrinsicSize.Min)
                ) {
                    Text(
                        stringResource(Res.string.new_update) + if (hasToUpdate != null) {
                            "\n\n" + stringResource(Res.string.required_update)
                        } else "",
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    val uriHandler = LocalUriHandler.current
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.inversePrimary,
                                UiConstants.RoundedCornerShape3
                            ),
                        shape = UiConstants.RoundedCornerShape3,
                        onClick = {
                            uriHandler.openUri("https://github.com/kroune/nine-mens-morris-app-kmp/releases")
                        }
                    ) {
                        Text(
                            stringResource(Res.string.update)
                        )
                    }
                }
            }
        }
}
