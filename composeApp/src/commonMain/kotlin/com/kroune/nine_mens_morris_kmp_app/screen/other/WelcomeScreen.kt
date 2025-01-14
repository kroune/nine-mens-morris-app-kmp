package com.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nine_mens_morris_kmp_app.common.BlackGrayColors
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.common.valueWithLifecycle
import com.kroune.nine_mens_morris_kmp_app.component.other.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountIdByJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.getScreenDpSize
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.TutorialScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.client_error
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.data_is_loading_wait
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.logged_in
import ninemensmorrisappkmp.composeapp.generated.resources.main_component
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.no_account
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_bot
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_friends
import ninemensmorrisappkmp.composeapp.generated.resources.play_online_game
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.settings
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(
    component: WelcomeScreenComponent
) {
    val scrollState = rememberScrollState(0)
    val snackbarHostState = remember { SnackbarHostState() }
    val topScreen = remember { mutableStateOf(true) }
    val coroutine = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.DarkGray,
                modifier = Modifier.height(50.dp)
            ) {
                BottomNavigationItem(
                    false, onClick = {
                        if (component.isInAccount.value == null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                            }
                            return@BottomNavigationItem
                        }
                        component.onEvent(WelcomeScreenEvent.NavigateToAccountView)
                    }, icon = {
                        // TODO: better error handling
                        when (
                            component.isInAccount.valueWithLifecycle()?.getOrDefault(false)) {
                            true -> {
                                Icon(
                                    painterResource(Res.drawable.logged_in),
                                    "logged in",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                )
                            }

                            false -> {
                                Icon(
                                    painterResource(Res.drawable.no_account),
                                    "no account found",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                )
                            }

                            null -> {
                                LoadingCircle(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                )
                BottomNavigationItem(
                    true,
                    onClick = {
                        val progress = scrollState.value.toFloat() / scrollState.maxValue
                        val scrollUp =
                            (topScreen.value && progress < 0.15f) || (!topScreen.value && progress <= 0.85f)
                        coroutine.launch {
                            scrollState.stopScroll()
                            scrollState.animateScrollTo(
                                if (!scrollUp) {
                                    component.onEvent(WelcomeScreenEvent.CloseTutorial)
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
                            "main component",
                            modifier = Modifier
                                .fillMaxHeight()
                        )
                    },
                )
                BottomNavigationItem(
                    false,
                    onClick = {
                        CoroutineScope(Dispatchers.Default).launch {
                            snackbarHostState.showSnackbar("This button doesn't have functionality for now, come back later")
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(Res.drawable.settings),
                            "settings",
                            modifier = Modifier
                                .fillMaxHeight()
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backgroundColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val onEvent: (WelcomeScreenEvent) -> Unit = { component.onEvent(it) }
            // show that this screen can be scrolled
            LaunchedEffect(Unit) {
                if (!component.hasSeenTutorial) {
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
                    coroutine.launch {
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
                    .verticalScroll(
                        state = scrollState, flingBehavior = CustomFlingBehaviour()
                    )
            ) {
                val screenSize = getScreenDpSize()
                val height =
                    screenSize.height - padding.calculateBottomPadding()
                val width = screenSize.width
                Box(
                    modifier = Modifier
                        .requiredHeight(height)
                        .requiredWidth(width),
                    contentAlignment = Alignment.Center
                ) {
                    RenderMainScreen(
                        component,
                        snackbarHostState
                    )
                }
                Spacer(Modifier.height(padding.calculateBottomPadding()))
                Box(
                    modifier = Modifier
                        .requiredHeight(height)
                        .requiredWidth(width)
                ) {
                    TutorialScreen()
                }
            }
        }
    }

    val exception = component.accountIdFailure ?: return
    val text: String = when (exception) {
        !is AccountIdByJwtTokenApiResponses -> {
            stringResource(Res.string.unknown_error)
        }

        is AccountIdByJwtTokenApiResponses.NetworkError -> {
            stringResource(Res.string.network_error)
        }

        AccountIdByJwtTokenApiResponses.ClientError -> {
            stringResource(Res.string.client_error)
        }

        AccountIdByJwtTokenApiResponses.CredentialsError -> {
            stringResource(Res.string.credentials_error)
        }

        AccountIdByJwtTokenApiResponses.ServerError -> {
            stringResource(Res.string.server_error)
        }

        else -> {
            error("kotlin broke")
        }
    }
    val scope = rememberCoroutineScope()
    scope.launch {
        snackbarHostState.showSnackbar(text)
    }
}

/**
 * renders main screen
 * where you can choose game mode or go to account settings
 */
@Composable
fun RenderMainScreen(
    component: WelcomeScreenComponent,
    snackbarHostState: SnackbarHostState
) {
    val screenSize = getScreenDpSize()
    val height = screenSize.height
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
                .height(height * 0.1f)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                component.onEvent(WelcomeScreenEvent.NavigateToGameWithFriend)
            },
            shape = RoundedCornerShape(5.dp),
            colors = BlackGrayColors()
        ) {
            Text(
                text = stringResource(Res.string.play_game_with_friends),
                color = Color.White,
                fontSize = 20.sp
            )
        }
        Button(
            modifier = Modifier
                .height(height * 0.1f)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                component.onEvent(WelcomeScreenEvent.NavigateToGameWithBot)
            },
            shape = RoundedCornerShape(5.dp),
            colors = BlackGrayColors()
        ) {
            Text(
                text = stringResource(Res.string.play_game_with_bot),
                color = Color.White,
                fontSize = 20.sp
            )
        }
        Button(
            modifier = Modifier
                .height(height * 0.1f)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (component.isInAccount.value == null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                    }
                    return@Button
                }
                component.onEvent(WelcomeScreenEvent.NavigateToOnlineGame)
            },
            shape = RoundedCornerShape(5.dp),
            colors = BlackGrayColors()
        ) {
            Text(
                text = stringResource(Res.string.play_online_game),
                color = Color.White,
                fontSize = 20.sp
            )
        }
        Button(
            modifier = Modifier
                .height(height * 0.1f)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                if (component.isInAccount.value == null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                    }
                    return@Button
                }
                component.onEvent(WelcomeScreenEvent.NavigateToLeaderboard)
            },
            shape = RoundedCornerShape(5.dp),
            colors = BlackGrayColors()
        ) {
            Text(
                text = stringResource(Res.string.leaderboard),
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}
