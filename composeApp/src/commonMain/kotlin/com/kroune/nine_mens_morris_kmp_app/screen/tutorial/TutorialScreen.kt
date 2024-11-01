package com.kroune.nine_mens_morris_kmp_app.screen.tutorial

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderNormalMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.getScreenSize
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderFlyingMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderIndicatorsTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderLoseTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderPlacementTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderRemovalMovesTutorialScreen
import com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderTriplesTutorialScreen
import kotlinx.coroutines.launch

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
    val screenSize = getScreenSize()
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
    LazyRow(
        modifier = Modifier
            .height(height.dp)
            .widthIn(0.dp, width.dp),
        state = listState,
        flingBehavior = CustomFlingBehaviour()
    ) {
        items(count = tutorialScreens.size,
            key = {
                it
            }) {
            Box(
                modifier = Modifier
                    .height(height.dp)
                    .width(width.dp)
            ) {
                tutorialScreens[it]()
            }
        }
    }
}