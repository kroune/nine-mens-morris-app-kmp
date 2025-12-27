package io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.LoadingCircle
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.TutorialScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.about
import ninemensmorrisappkmp.composeapp.generated.resources.close
import ninemensmorrisappkmp.composeapp.generated.resources.data_is_loading_wait
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.logged_in
import ninemensmorrisappkmp.composeapp.generated.resources.main_component
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.no_account
import ninemensmorrisappkmp.composeapp.generated.resources.offline
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_bot
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_friends
import ninemensmorrisappkmp.composeapp.generated.resources.play_online_game
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(
    state: WelcomeScreenState,
    onEvent: (WelcomeScreenEvent) -> Unit,
) {
    val scrollState = rememberScrollState(0)
    val snackbarHostState = remember { SnackbarHostState() }
    val topScreen = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            WelcomeScreenBottomBar(
                state,
                scope,
                snackbarHostState,
                onEvent,
                scrollState,
                topScreen,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(),
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            // show that this screen can be scrolled
            LaunchedEffect(Unit) {
                if (!state.hasSeenTutorial) {
                    delay(250L)
                    scrollState.stopScroll()
                    scrollState.animateScrollTo(
                        scrollState.maxValue,
                        animationSpec = tween(durationMillis = 750, easing = LinearEasing)
                    )
                }
            }
            val customFlingBehaviour = object : FlingBehavior {
                override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                    val progress = scrollState.value.toFloat() / scrollState.maxValue
                    val scrollUp =
                        (topScreen.value && progress < 0.15f) || (!topScreen.value && progress <= 0.85f)
                    topScreen.value = scrollUp
                    scope.launch {
                        scrollState.stopScroll()
                        scrollState.animateScrollTo(
                            if (scrollUp) {
                                onEvent(WelcomeScreenEvent.CloseTutorial)
                                0
                            } else {
                                scrollState.maxValue
                            },
                            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                        )
                    }
                    return 0f
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState, flingBehavior = customFlingBehaviour)
            ) {
                RenderMainScreen(
                    state = state,
                    onEvent = { onEvent(it) },
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier
                        .requiredHeight(this@BoxWithConstraints.maxHeight)
                        .requiredWidth(this@BoxWithConstraints.maxWidth),
                )
                TutorialScreen(
                    modifier = Modifier
                        .requiredHeight(this@BoxWithConstraints.maxHeight)
                        .requiredWidth(this@BoxWithConstraints.maxWidth),
                )
            }
            HandleWelcomeScreenError(
                result = state.accountIdFailure,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@Composable
private fun WelcomeScreenBottomBar(
    state: WelcomeScreenState,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onEvent: (WelcomeScreenEvent) -> Unit,
    scrollState: ScrollState,
    topScreen: MutableState<Boolean>
) {
    NavigationBar(
        modifier = Modifier
            .semantics {
                contentDescription = "bottom navigation bar"
            }
    ) {
        NavigationBarItem(
            false,
            onClick = {
                if (state.isInAccount == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            getString(Res.string.data_is_loading_wait)
                        )
                    }
                    return@NavigationBarItem
                }
                onEvent(WelcomeScreenEvent.NavigateToAccountView)
            },
            icon = {
                when (val isInAccount = state.isInAccount) {
                    null -> {
                        LoadingCircle(
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "loading account information"
                                }
                        )
                    }

                    is CheckJwtTokenApiResponses.Success -> {
                        if (isInAccount.result)
                            Icon(
                                painterResource(Res.drawable.logged_in),
                                "account information was loaded",
                            )
                        else
                            Icon(
                                painterResource(Res.drawable.no_account),
                                "account information wasn't found",
                            )
                    }

                    else -> {
                        Icon(
                            painterResource(Res.drawable.offline),
                            "you are offline",
                        )
                        val errorText = when (isInAccount) {
                            is CheckJwtTokenApiResponses.NetworkError -> {
                                stringResource(Res.string.network_error)
                            }

                            is CheckJwtTokenApiResponses.ServerError -> {
                                stringResource(Res.string.server_error)
                            }

                            is CheckJwtTokenApiResponses.Success -> return@NavigationBarItem

                            is CheckJwtTokenApiResponses.UnknownError -> {
                                stringResource(Res.string.unknown_error)
                            }
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(errorText)
                        }
                    }
                }
            },
            modifier = Modifier.height(32.dp),
        )
        NavigationBarItem(
            true,
            onClick = {
                val progress = scrollState.value.toFloat() / scrollState.maxValue
                val scrollUp =
                    (topScreen.value && progress < 0.15f) || (!topScreen.value && progress <= 0.85f)
                scope.launch {
                    scrollState.stopScroll()
                    scrollState.animateScrollTo(
                        if (!scrollUp) {
                            onEvent(WelcomeScreenEvent.CloseTutorial)
                            0
                        } else {
                            scrollState.maxValue
                        },
                        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
                    )
                }
            },
            icon = {
                Icon(
                    painterResource(Res.drawable.main_component),
                    "scroll up or down",
                )
            },
            modifier = Modifier.height(32.dp),
        )
        NavigationBarItem(
            false,
            onClick = {
                onEvent(WelcomeScreenEvent.NavigateToAboutScreen)
            },
            icon = {
                Icon(
                    painterResource(Res.drawable.about),
                    "go to settings button",
                )
            },
            modifier = Modifier.height(32.dp),
        )
    }
}

/**
 * renders main screen
 * where you can choose game mode or go to account settings
 */
@Composable
fun RenderMainScreen(
    state: WelcomeScreenState,
    onEvent: (WelcomeScreenEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        IconButton(
            onClick = { onEvent(WelcomeScreenEvent.NavigateBack) }
        ) {
            Icon(
                painterResource(Res.drawable.close),
                "close button",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        BoxWithConstraints(
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            val spacing = this.maxHeight * 0.05f
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = { onEvent(WelcomeScreenEvent.NavigateToGameWithFriend) },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(Res.string.play_game_with_friends),
                        fontSize = 20.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = { onEvent(WelcomeScreenEvent.NavigateToGameWithBot) },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(Res.string.play_game_with_bot),
                        fontSize = 20.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        if (state.isInAccount == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    getString(Res.string.data_is_loading_wait)
                                )
                            }
                            return@Button
                        }
                        onEvent(WelcomeScreenEvent.NavigateToOnlineGame)
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(Res.string.play_online_game),
                        fontSize = 20.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        if (state.isInAccount == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    getString(Res.string.data_is_loading_wait)
                                )
                            }
                            return@Button
                        }
                        onEvent(WelcomeScreenEvent.NavigateToLeaderboard)
                    },
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = stringResource(Res.string.leaderboard),
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}
