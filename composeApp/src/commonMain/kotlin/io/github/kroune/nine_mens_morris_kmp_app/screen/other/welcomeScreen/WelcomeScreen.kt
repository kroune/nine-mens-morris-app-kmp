package io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import io.github.kroune.nine_mens_morris_kmp_app.getScreenDpSize
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.LoadingCircle
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.TutorialScreen
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
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(64.dp)
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
                        val isInAccount = state.isInAccount
                        when (isInAccount) {
                            null -> {
                                LoadingCircle(
                                    modifier = Modifier
                                        .size(32.dp)
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
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                else
                                    Icon(
                                        painterResource(Res.drawable.no_account),
                                        "account information wasn't found",
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                            }

                            else -> {
                                Icon(
                                    painterResource(Res.drawable.offline),
                                    "you are offline",
                                    modifier = Modifier
                                        .size(32.dp)
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
                    }
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
                                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                            )
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(Res.drawable.main_component),
                            "scroll up or down",
                            modifier = Modifier
                                .size(32.dp)
                        )
                    },
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
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val onEvent: (WelcomeScreenEvent) -> Unit = { onEvent(it) }
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
            class CustomFlingBehaviour : FlingBehavior {
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
                    .fillMaxWidth()
                    .verticalScroll(
                        state = scrollState, flingBehavior = CustomFlingBehaviour()
                    )
            ) {
                val screenSize = getScreenDpSize()
                val height =
                    screenSize.height - padding.calculateBottomPadding() - padding.calculateTopPadding()
                val width = screenSize.width
                Box(
                    modifier = Modifier
                        .requiredHeight(height)
                        .requiredWidth(width)
                ) {
                    IconButton(
                        {
                            onEvent(WelcomeScreenEvent.NavigateBack)
                        }
                    ) {
                        Icon(
                            painterResource(Res.drawable.close),
                            "close button",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    RenderMainScreen(
                        state,
                        {
                            onEvent(it)
                        },
                        snackbarHostState
                    )
                }
                Box(
                    modifier = Modifier
                        .requiredHeight(height)
                        .requiredWidth(width),
                    contentAlignment = Alignment.Center
                ) {
                    TutorialScreen()
                }
            }
            HandleWelcomeScreenError(
                state.accountIdFailure,
                snackbarHostState
            )
        }
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
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val screenSize = getScreenDpSize()
    val height = screenSize.height
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(
                height * 0.05f, Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    onEvent(WelcomeScreenEvent.NavigateToGameWithFriend)
                },
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
                onClick = {
                    onEvent(WelcomeScreenEvent.NavigateToGameWithBot)
                },
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
                    fontSize = 20.sp
                )
            }
        }
    }
}
