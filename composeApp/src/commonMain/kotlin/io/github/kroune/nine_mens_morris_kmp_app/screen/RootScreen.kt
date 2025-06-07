package io.github.kroune.nine_mens_morris_kmp_app.screen

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
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.kroune.nine_mens_morris_kmp_app.BuildKonfig
import io.github.kroune.nine_mens_morris_kmp_app.component.RootComponent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Child
import io.github.kroune.nine_mens_morris_kmp_app.navigation.Configuration
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreen(component: RootComponent) {
    val stackAnimation = stackAnimation<Configuration, Child> { it ->
        it.configuration.customAnimation
    }
    AppTheme {
        Scaffold {
            Box(
                modifier = Modifier.padding(it)
            ) {
                val childStack by component.childStack.subscribeAsState()
                Children(
                    stack = childStack,
                    animation = stackAnimation,
                ) { child ->
                    val instance = child.instance
                    when (instance) {
                        is Child.AppStartAnimationScreenChild -> {
                            with(instance.component) {
                                AppStartAnimationScreen(
                                    onEvent = { onEvent(it) }
                                )
                            }
                        }

                        is Child.WelcomeScreenChild -> {
                            with(instance.component) {
                                WelcomeScreen(
                                    onEvent = { onEvent(it) },
                                    state = state.collectValue()
                                )
                            }
                        }

                        is Child.ViewAccountScreenChild -> {
                            with(instance.component) {
                                ViewAccountScreen(
                                    onEvent = { onEvent(it) },
                                    state = state.collectValue()
                                )
                            }
                        }

                        is Child.ViewOwnAccountScreenChild -> {
                            with(instance.component) {
                                ViewOwnAccountScreen(
                                    onEvent = { onEvent(it) },
                                    state = state.collectValue()
                                )
                            }
                        }

                        is Child.SignUpScreenChild -> {
                            with(instance.component) {
                                SignUpScreen(
                                    component = state.collectValue(),
                                    onEvent = { onEvent(it) }
                                )
                            }
                        }

                        is Child.SignInScreenChild -> {
                            with(instance.component) {
                                SignInScreen(
                                    state = state.collectValue(),
                                    onEvent = { onEvent(it) }
                                )
                            }
                        }

                        is Child.GameWithFriendChild -> {
                            with(instance.component) {
                                GameWithFriendScreen(
                                    state = state.collectValue(),
                                    onEvent = { onEvent(it) }
                                )
                            }
                        }

                        is Child.GameWithBotChild -> {
                            with(instance.component) {
                                GameWithBotScreen(
                                    state = state.collectValue(),
                                    onEvent = { onEvent(it) }
                                )
                            }
                        }

                        is Child.SearchingForGameChild -> {
                            SearchingForGameScreen(instance.component)
                        }

                        is Child.OnlineGameChild -> {
                            with(instance.component) {
                                OnlineGameScreen(
                                    { onEvent(it) },
                                    state.collectValue()
                                )
                            }
                        }

                        is Child.LeaderboardChild -> {
                            with(instance.component) {
                                LeaderboardScreen(
                                    { onEvent(it) },
                                    state.collectValue()
                                )
                            }
                        }

                        is Child.AboutChild -> {
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
                run {
                    val state = component.state.collectValue()
                    val lastVersion = state.lastVersion.let {
                        if (it !is AppLastVersionApiResponse.Success)
                            return@run
                        if (it.lastVersion == null)
                            return@run
                        if (it.lastVersion <= BuildKonfig.versionInt)
                            return@run
                        it.lastVersion
                    }
                    val hasToUpdate = state.requiredVersion.let {
                        if (it !is RequiredVersionApiResponse.Success)
                            return@run
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
            }
        }
    }
}