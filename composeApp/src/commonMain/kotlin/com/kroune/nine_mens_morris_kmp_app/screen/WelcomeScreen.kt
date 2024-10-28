package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kroune.nine_mens_morris_kmp_app.common.BlackGrayColors
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.common.ParallelogramShape
import com.kroune.nine_mens_morris_kmp_app.common.triangleShape
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.getScreenSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.logged_in
import ninemensmorrisappkmp.composeapp.generated.resources.no_account
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_bot
import ninemensmorrisappkmp.composeapp.generated.resources.play_game_with_friends
import ninemensmorrisappkmp.composeapp.generated.resources.play_online_game
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
fun WelcomeScreen(
    component: WelcomeScreenComponent
) {
    val isInAccount = component.isInAccount
    val checkingJwtTokenJob = component.checkingJwtTokenJob.collectAsStateWithLifecycle().value
    val onEvent: (WelcomeScreenEvent) -> Unit = { component.onEvent(it) }
    val hasSeen = component.hasSeenTutorial
    val viewAccountDataLoadingOverlay = remember { mutableStateOf(false) }
    val playOnlineGameOverlay = remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    // we check this to prevent race condition, since if user is searching for game
    // viewing account gets less priority
    if (viewAccountDataLoadingOverlay.value && !playOnlineGameOverlay.value) {
        HandleAccountViewOverlay(
            isInAccount,
            checkingJwtTokenJob,
            onEvent
        )
    }
    if (playOnlineGameOverlay.value) {
        HandleOverlay()
    }
    val scrollState = rememberScrollState(if (!hasSeen) Int.MAX_VALUE else 0)
    val topScreen = remember { mutableStateOf(true) }
    if (scrollState.value == 0) {
        onEvent(WelcomeScreenEvent.CloseTutorial)
    }
    class CustomFlingBehaviour : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            val progress = scrollState.value.toFloat() / scrollState.maxValue
            val scrollUp =
                (topScreen.value && progress < 0.15f) || (!topScreen.value && progress <= 0.85f)
            topScreen.value = scrollUp
            coroutine.launch {
                scrollState.animateScrollTo(
                    if (scrollUp) 0 else scrollState.maxValue,
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
        RenderMainScreen(
            isInAccount,
            checkingJwtTokenJob,
            viewAccountDataLoadingOverlay,
            onEvent
        )
//        TutorialScreen(resources).InvokeRender()
    }
    component.popupToDraw()
}

/**
 * renders main screen
 * where you can choose game mode or go to account settings
 */
@Composable
private fun RenderMainScreen(
    isInAccount: Boolean?,
    checkingJwtTokenJob: Job,
    viewAccountDataLoadingOverlay: MutableState<Boolean>,
    onEvent: (WelcomeScreenEvent) -> Unit
) {
    var screenSize = getScreenSize()
    val width = screenSize.width
    val height = screenSize.height
    Box(
        modifier = Modifier.size(width.dp, height.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (height * 0.2).dp, bottom = (height * 0.2).dp),
            verticalArrangement = Arrangement.spacedBy(
                (height * 0.05).dp, Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    onEvent(WelcomeScreenEvent.ClickGameWithFriendButton)
                },
                shape = RoundedCornerShape(5.dp),
                colors = BlackGrayColors()
            ) {
                Text(
                    text = stringResource(Res.string.play_game_with_friends),
                    color = Color.White
                )
            }
            Button(
                onClick = {
                    onEvent(WelcomeScreenEvent.ClickGameWithBotButton)
                },
                shape = RoundedCornerShape(5.dp),
                colors = BlackGrayColors()
            ) {
                Text(
                    text = stringResource(Res.string.play_game_with_bot), color = Color.White
                )
            }
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        checkingJwtTokenJob.join()
                        if (isInAccount == true) {
                            onEvent(WelcomeScreenEvent.ClickOnlineGameButton)
                        } else {
                            checkingJwtTokenJob.join()
                            onEvent(WelcomeScreenEvent.ClickOnlineGameButton)
                        }
                    }
//                    playOnlineGameOverlay.value = true
                },
                shape = RoundedCornerShape(5.dp),
                colors = BlackGrayColors()
            ) {
                Text(
                    text = stringResource(Res.string.play_online_game), color = Color.White
                )
            }
        }
        ViewAccountElement(
            viewAccountDataLoadingOverlay,
            isInAccount
        )
    }
}

/**
 * view account element
 *
 * by default it is a simple triangle
 * you can drag it down or click on it, to view your own account (you will be prompted to login
 * screen if you aren't logged in)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.ViewAccountElement(
    viewAccountDataLoadingOverlay: MutableState<Boolean>,
    isInAccount: Boolean?
) {
    val coroutine = rememberCoroutineScope()
    var screenSize = getScreenSize()
    val width = screenSize.width
    val height = screenSize.height
    val offset = remember { mutableStateOf(0f) }
    val startTriangleLength = min(width, height) / 4f
    val offsetToFillRightBottomCorner = height - startTriangleLength
    val isTriangle = remember {
        derivedStateOf {
            offset.value < offsetToFillRightBottomCorner
        }
    }
    val canDrag = remember { mutableStateOf(true) }
    Box(modifier = Modifier
        .then(
            if (isTriangle.value) {
                Modifier.size((offset.value + startTriangleLength).dp)
            } else {
                Modifier.fillMaxSize()
            }
        )
        .align(Alignment.TopEnd)
        .draggable2D(state = rememberDraggable2DState { delta ->
            offset.value = ((-delta.x + delta.y) / 2f + offset.value).coerceAtLeast(0f)
        }, enabled = canDrag.value, onDragStopped = {
            // if we should animate transition to the start pos or
            // continue animation (switch to another screen)
            val shouldRollBack = offset.value + startTriangleLength < width / 2
            val destination = if (shouldRollBack) {
                0f
            } else {
                offsetToFillRightBottomCorner + width
            }
            canDrag.value = false
            coroutine.launch {
                animate(
                    offset.value, destination, animationSpec = tween(
                        durationMillis = 500, easing = LinearEasing
                    )
                ) { value, _ ->
                    offset.value = value
                }
                if (!shouldRollBack) {
                    viewAccountDataLoadingOverlay.value = true
                }
                canDrag.value = true
            }
        })
        .background(
            Color.DarkGray,
            if (isTriangle.value) triangleShape else
                ParallelogramShape(bottomLineLeftOffset = with(
                    LocalDensity.current
                ) {
                    (offset.value - offsetToFillRightBottomCorner).dp.toPx()
                })
        ), contentAlignment = { size, space, _ ->
        IntOffset(
            space.width / 2, space.height / 2 - size.height
        )
    }) {
        IconButton(
            onClick = {
                viewAccountDataLoadingOverlay.value = true
            },
            modifier = Modifier.size((startTriangleLength / 2f).dp)
        ) {
            when (isInAccount) {
                true -> {
                    Icon(painterResource(Res.drawable.logged_in), "logged in")
                }

                false -> {
                    Icon(painterResource(Res.drawable.no_account), "no account found")
                }

                null -> {
                    LoadingCircle()
                }
            }
        }
    }
}

/**
 * decides where should we be navigated
 */
@Composable
private fun HandleAccountViewOverlay(
    isInAccount: Boolean?,
    checkingJwtTokenJob: Job,
    onEvent: (WelcomeScreenEvent) -> Unit,
) {
    CoroutineScope(Dispatchers.Default).launch {
        checkingJwtTokenJob.join()
        if (isInAccount == true) {
            onEvent(WelcomeScreenEvent.AccountViewButton)
        } else {
            checkingJwtTokenJob.join()
            onEvent(WelcomeScreenEvent.AccountViewButton)
        }
    }
    HandleOverlay()
}

/**
 * draw overlay
 */
@Composable
private fun HandleOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(1.5f)
            .background(Color(0, 0, 0, 40)),
        contentAlignment = Alignment.Center
    ) {
        // we shouldn't be stuck on this screen, since network client timeout is 5 s
        LoadingCircle()
    }
}