package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RemoteAccountInfoDataSource

@Composable
fun WelcomeScreen(
    component: WelcomeScreenComponent
) {
//    RenderWelcomeScreen()
}
///**
// * Renders welcome screen
// */
//@Composable
//fun RenderWelcomeScreen(
//    accountId: Long?,
//    checkJwtToken: suspend () -> Result<Boolean>,
//    hasJwtToken: () -> Boolean,
//    resources: Resources,
//    navController: NavHostController,
//    hasSeen: Boolean,
//    onTutorialHide: () -> Unit
//) {
//    val viewAccountDataLoadingOverlay = remember { mutableStateOf(false) }
//    val playOnlineGameOverlay = remember { mutableStateOf(false) }
//    val coroutine = rememberCoroutineScope()
//    // we check this to prevent race condition, since if user is searching for game
//    // viewing account gets less priority
//    if (viewAccountDataLoadingOverlay.value && !playOnlineGameOverlay.value) {
//        HandleAccountViewOverlay(
//            accountId,
//            hasJwtToken,
//            navController
//        )
//    }
//    if (playOnlineGameOverlay.value) {
//        HandleOverlay()
//    }
//    val scrollState = rememberScrollState(if (!hasSeen) Int.MAX_VALUE else 0)
//    val topScreen = remember { mutableStateOf(true) }
//    if (scrollState.value == 0) {
//        onTutorialHide()
//    }
//    class CustomFlingBehaviour : FlingBehavior {
//        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
//            val progress = scrollState.value.toFloat() / scrollState.maxValue
//            val scrollUp =
//                (topScreen.value && progress < 0.15f) || (!topScreen.value && progress <= 0.85f)
//            topScreen.value = scrollUp
//            coroutine.launch {
//                scrollState.animateScrollTo(
//                    if (scrollUp) 0 else scrollState.maxValue,
//                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
//                )
//            }
//            return 0f
//        }
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(
//                state = scrollState, flingBehavior = CustomFlingBehaviour()
//            )
//    ) {
//        RenderMainScreen(
//            resources,
//            navController,
//            playOnlineGameOverlay,
//            viewAccountDataLoadingOverlay,
//            hasJwtToken,
//            checkJwtToken
//        )
//        TutorialScreen(resources).InvokeRender()
//    }
//}
//
///**
// * renders main screen
// * where you can choose game mode or go to account settings
// */
//@Composable
//private fun RenderMainScreen(
//    resources: Resources,
//    navController: NavHostController?,
//    playOnlineGameOverlay: MutableState<Boolean>,
//    viewAccountDataLoadingOverlay: MutableState<Boolean>,
//    hasJwtToken: () -> Boolean,
//    checkJwtToken: suspend () -> Result<Boolean>
//) {
//    val width = LocalConfiguration.current.screenWidthDp
//    val height = LocalConfiguration.current.screenHeightDp
//    Box(
//        modifier = Modifier.size(width.dp, height.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = (height * 0.2).dp, bottom = (height * 0.2).dp),
//            verticalArrangement = Arrangement.spacedBy(
//                (height * 0.05).dp, Alignment.CenterVertically
//            ),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(
//                modifier = Modifier.align(Alignment.CenterHorizontally),
//                onClick = {
//                    navController?.navigateSingleTopTo(Navigation.GameWithFriend)
//                },
//                shape = RoundedCornerShape(5.dp),
//                colors = ButtonColors(Color.Black, Color.Black, Color.Gray, Color.Gray)
//            ) {
//                Text(
//                    text = resources.getString(R.string.play_game_with_friends),
//                    color = Color.White
//                )
//            }
//            Button(
//                onClick = {
//                    navController?.navigateSingleTopTo(Navigation.GameWithBot)
//                },
//                shape = RoundedCornerShape(5.dp),
//                colors = ButtonColors(Color.Black, Color.Black, Color.Gray, Color.Gray)
//            ) {
//                Text(
//                    text = resources.getString(R.string.play_game_with_bot), color = Color.White
//                )
//            }
//            Button(
//                onClick = {
//                    playOnlineGameOverlay.value = true
//                    CoroutineScope(Dispatchers.IO).launch {
//                        if (checkJwtToken().getOrNull() == true) {
//                            withContext(Dispatchers.Main) {
//                                navController?.navigateSingleTopTo(Navigation.SearchingOnlineGame)
//                            }
//                        } else {
//                            withContext(Dispatchers.Main) {
//                                navController?.navigateSingleTopTo(
//                                    Navigation.SignIn(
//                                        Navigation.SearchingOnlineGame
//                                    )
//                                )
//                            }
//                        }
//                    }
//                },
//                shape = RoundedCornerShape(5.dp),
//                colors = ButtonColors(Color.Black, Color.Black, Color.Gray, Color.Gray)
//            ) {
//                Text(
//                    text = resources.getString(R.string.play_online_game), color = Color.White
//                )
//            }
//        }
//        ViewAccountElement(
//            viewAccountDataLoadingOverlay = viewAccountDataLoadingOverlay,
//            hasJwtToken = hasJwtToken
//        )
//    }
//}
//
///**
// * view account element
// *
// * by default it is a simple triangle
// * you can drag it down or click on it, to view your own account (you will be prompted to login
// * screen if you aren't logged in)
// */
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun BoxScope.ViewAccountElement(
//    viewAccountDataLoadingOverlay: MutableState<Boolean>,
//    hasJwtToken: () -> Boolean
//) {
//    val coroutine = rememberCoroutineScope()
//    val width = LocalConfiguration.current.screenWidthDp
//    val height = LocalConfiguration.current.screenHeightDp
//    val offset = remember { mutableStateOf(0f) }
//    val startTriangleLength = min(width, height) / 4f
//    val offsetToFillRightBottomCorner = height - startTriangleLength
//    val isTriangle = remember {
//        derivedStateOf {
//            offset.value < offsetToFillRightBottomCorner
//        }
//    }
//    val canDrag = remember { mutableStateOf(true) }
//    Box(modifier = Modifier
//        .then(
//            if (isTriangle.value) size((offset.value + startTriangleLength).dp)
//            else Modifier.fillMaxSize()
//        )
//        .align(Alignment.TopEnd)
//        .draggable2D(state = rememberDraggable2DState { delta ->
//            offset.value = ((-delta.x + delta.y) / 2f + offset.value).coerceAtLeast(0f)
//        }, enabled = canDrag.value, onDragStopped = {
//            // if we should animate transition to the start pos or
//            // continue animation (switch to another screen)
//            val shouldRollBack = offset.value + startTriangleLength < width / 2
//            val destination = if (shouldRollBack) {
//                0f
//            } else {
//                offsetToFillRightBottomCorner + width
//            }
//            canDrag.value = false
//            coroutine.launch {
//                animate(
//                    offset.value, destination, animationSpec = tween(
//                        durationMillis = 500, easing = LinearEasing
//                    )
//                ) { value, _ ->
//                    offset.value = value
//                }
//                if (!shouldRollBack) {
//                    viewAccountDataLoadingOverlay.value = true
//                }
//                canDrag.value = true
//            }
//        })
//        .background(
//            Color.DarkGray,
//            if (isTriangle.value) triangleShape else
//                ParallelogramShape(bottomLineLeftOffset = with(
//                    LocalDensity.current
//                ) {
//                    (offset.value - offsetToFillRightBottomCorner).dp.toPx()
//                })
//        ), contentAlignment = { size, space, _ ->
//        IntOffset(
//            space.width / 2, space.height / 2 - size.height
//        )
//    }) {
//        IconButton(
//            onClick = {
//                viewAccountDataLoadingOverlay.value = true
//            }, modifier = size((startTriangleLength / 2f).dp)
//        ) {
//            if (hasJwtToken()) {
//                Icon(painterResource(R.drawable.logged_in), "logged in")
//            } else {
//                Icon(painterResource(R.drawable.no_account), "no account found")
//            }
//        }
//    }
//}
//
///**
// * decides where should we be navigated
// */
//@Composable
//private fun HandleAccountViewOverlay(
//    accountId: Long?,
//    hasJwtToken: () -> Boolean,
//    navController: NavHostController?
//) {
//    val accountId = accountId
//    if (!hasJwtToken()) {
//        // no valid account, we need to sign in
//        navController?.navigateSingleTopTo(
//            Navigation.SignIn(
//                Navigation.ViewAccount(
//                    -1L
//                )
//            )
//        )
//    } else {
//        if (accountId != null) {
//            navController?.navigateSingleTopTo(Navigation.ViewAccount(accountId))
//        }
//    }
//    HandleOverlay()
//}
//
///**
// * draw overlay
// */
//@Composable
//private fun HandleOverlay() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .zIndex(Float.MAX_VALUE)
//            .background(Color(0, 0, 0, 50)),
//        contentAlignment = Alignment.Center
//    ) {
//        // we shouldn't be stuck on this screen, since network client timeout is 5 s
//        LoadingCircle()
//    }
//}
