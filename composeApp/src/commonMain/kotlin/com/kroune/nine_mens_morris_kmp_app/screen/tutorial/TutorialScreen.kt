package com.kroune.nine_mens_morris_kmp_app.screen.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.kroune.nine_mens_morris_kmp_app.getScreenDpSize
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderFlyingMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderIndicatorsTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderLoseTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderNormalMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderPlacementTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderRemovalMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderTriplesTutorialScreen
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.left_arrow
import ninemensmorrisappkmp.composeapp.generated.resources.right_arrow
import org.jetbrains.compose.resources.painterResource

/**
 * stores order of tutorials (used for slider)
 */
private val tutorialScreens: List<@Composable () -> Unit> = listOf(
    {
        RenderIndicatorsTutorialScreen()
    },
    {
        RenderLoseTutorialScreen()
    },
    {
        RenderPlacementTutorialScreen()
    },
    {
        RenderNormalMovesTutorialScreen()
    },
    {
        RenderFlyingMovesTutorialScreen()
    },
    {
        RenderTriplesTutorialScreen()
    },
    {
        RenderRemovalMovesTutorialScreen()
    }
)

@Composable
fun TutorialScreen() {
    val screenSize = getScreenDpSize()
    val width = screenSize.width
    val height = screenSize.height
    val coroutine = rememberCoroutineScope()
    val currentScreenIndex = remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    class CustomFlingBehaviour : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            val scrollWidth = listState.layoutInfo.viewportSize.width
            when {
                (listState.firstVisibleItemIndex < currentScreenIndex.intValue &&
                        listState.firstVisibleItemScrollOffset <= scrollWidth * 0.85) -> {
                    currentScreenIndex.intValue--
                }

                (listState.firstVisibleItemScrollOffset > 0 &&
                        listState.firstVisibleItemScrollOffset >= scrollWidth * 0.15) -> {
                    currentScreenIndex.intValue++
                }
            }
            coroutine.launch {
                listState.animateScrollToItem(currentScreenIndex.intValue)
            }
            return 0f
        }
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(20.dp)
                .width(width)
                .zIndex(5f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                coroutine.launch {
                    currentScreenIndex.intValue =
                        (currentScreenIndex.intValue + tutorialScreens.size - 1) % tutorialScreens.size
                    listState.animateScrollToItem(currentScreenIndex.intValue)
                }
            }) {
                Icon(
                    painter = painterResource(Res.drawable.left_arrow), "to the left"
                )
            }
            IconButton(onClick = {
                coroutine.launch {
                    currentScreenIndex.intValue =
                        (currentScreenIndex.intValue + 1) % tutorialScreens.size
                    listState.animateScrollToItem(currentScreenIndex.intValue)
                }
            }) {
                Icon(
                    painter = painterResource(Res.drawable.right_arrow), "to the right"
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .height(height)
                .width(width),
            state = listState,
            flingBehavior = CustomFlingBehaviour()
        ) {
            items(count = tutorialScreens.size,
                key = {
                    it
                }) {
                Box(
                    modifier = Modifier
                        .height(height)
                        .width(width)
                ) {
                    tutorialScreens[it]()
                }
            }
        }
        Row(
            modifier = Modifier
                .zIndex(5f)
                .height(height)
                .width(tutorialScreens.size * 3 * 7.dp)
                .padding(bottom = 50.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tutorialScreens.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .run {
                            if (currentScreenIndex.value == index) {
                                background(Color.Blue)
                            } else {
                                background(Color.White)
                            }
                        }
                )
            }
        }
    }
}