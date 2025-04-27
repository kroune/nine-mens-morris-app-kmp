package io.github.kroune.nine_mens_morris_kmp_app.screen.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import io.github.kroune.nine_mens_morris_kmp_app.common.collectValue
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponentI
import io.github.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.getScreenDpSize
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.TutorialScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.close
import ninemensmorrisappkmp.composeapp.generated.resources.offline
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
    component: WelcomeScreenComponentI
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
                    .height(55.dp)
                    .semantics {
                        contentDescription = "bottom navigation bar"
                    }
            ) {
                NavigationBarItem(
                    false, onClick = {
                        if (component.isInAccount.value == null) {
                            CoroutineScope(Dispatchers.Default).launch {
                                snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                            }
                            return@NavigationBarItem
                        }
                        component.onEvent(WelcomeScreenEvent.NavigateToAccountView)
                    },
                    icon = {
                        val isInAccount = component.isInAccount.collectValue()
                        when (isInAccount) {
                            null -> {
                                LoadingCircle(
                                    modifier = Modifier
                                        .semantics {
                                            contentDescription = "loading account information"
                                        }
                                        .fillMaxHeight()
                                )
                            }

                            is CheckJwtTokenApiResponses.Success -> {
                                if (isInAccount.result)
                                    Icon(
                                        painterResource(Res.drawable.logged_in),
                                        "account information was loaded",
                                        modifier = Modifier
                                            .fillMaxHeight()
                                    )
                                else
                                    Icon(
                                        painterResource(Res.drawable.no_account),
                                        "account information wasn't found",
                                        modifier = Modifier
                                            .fillMaxHeight()
                                    )
                            }

                            else -> {
                                Icon(
                                    painterResource(Res.drawable.offline),
                                    "you are offline",
                                    modifier = Modifier
                                        .fillMaxHeight()
                                )
                                val errorText = when (isInAccount) {
                                    CheckJwtTokenApiResponses.NetworkError -> {
                                        stringResource(Res.string.network_error)
                                    }

                                    CheckJwtTokenApiResponses.ServerError -> {
                                        stringResource(Res.string.server_error)
                                    }

                                    is CheckJwtTokenApiResponses.Success -> return@NavigationBarItem

                                    CheckJwtTokenApiResponses.UnknownError -> {
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
                            "scroll up or down",
                            modifier = Modifier
                                .fillMaxHeight()
                        )
                    },
                )
                NavigationBarItem(
                    false,
                    onClick = {
                        CoroutineScope(Dispatchers.Default).launch {
                            snackbarHostState.showSnackbar("This button doesn't have functionality for now, come back later")
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(Res.drawable.settings),
                            "go to settings button",
                            modifier = Modifier
                                .fillMaxHeight(),
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.Transparent
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
                    IconButton({
                        component.onEvent(WelcomeScreenEvent.NavigateBack)
                    }) {
                        Icon(
                            painterResource(Res.drawable.close),
                            "close button",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    RenderMainScreen(
                        component,
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
            HandleError(component.accountIdFailure, snackbarHostState)
        }
    }
}

@Composable
private fun HandleError(
    result: AccountIdByJwtTokenApiResponses?,
    snackbarHostState: SnackbarHostState
) {
    val text: String = when (result) {
        is AccountIdByJwtTokenApiResponses.UnknownError -> {
            stringResource(Res.string.unknown_error)
        }

        is AccountIdByJwtTokenApiResponses.NetworkError -> {
            stringResource(Res.string.network_error)
        }

        is AccountIdByJwtTokenApiResponses.CredentialsError -> {
            stringResource(Res.string.credentials_error)
        }

        is AccountIdByJwtTokenApiResponses.ServerError -> {
            stringResource(Res.string.server_error)
        }

        is AccountIdByJwtTokenApiResponses.Success, null -> return
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
    component: WelcomeScreenComponentI,
    snackbarHostState: SnackbarHostState
) {
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
                    component.onEvent(WelcomeScreenEvent.NavigateToGameWithFriend)
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
                    component.onEvent(WelcomeScreenEvent.NavigateToGameWithBot)
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
                    if (component.isInAccount.value == null) {
                        CoroutineScope(Dispatchers.Default).launch {
                            snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                        }
                        return@Button
                    }
                    component.onEvent(WelcomeScreenEvent.NavigateToOnlineGame)
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
                    if (component.isInAccount.value == null) {
                        CoroutineScope(Dispatchers.Default).launch {
                            snackbarHostState.showSnackbar(getString(Res.string.data_is_loading_wait))
                        }
                        return@Button
                    }
                    component.onEvent(WelcomeScreenEvent.NavigateToLeaderboard)
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
