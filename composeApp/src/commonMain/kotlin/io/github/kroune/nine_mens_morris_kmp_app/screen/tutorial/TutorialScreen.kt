package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderFlyingMovesTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderIndicatorsTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderLoseTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderNormalMovesTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderPlacementTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderRemovalMovesTutorialScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements.RenderTriplesTutorialScreen
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
fun TutorialScreen(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val currentScreenIndex = remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    val flingBehaviour = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            val scrollWidth = listState.layoutInfo.viewportSize.width
            when {
                (listState.firstVisibleItemIndex < currentScreenIndex.intValue &&
                        listState.firstVisibleItemScrollOffset <= scrollWidth * 0.85) -> {
                    currentScreenIndex.intValue =
                        (currentScreenIndex.intValue - 1) % tutorialScreens.size
                }

                (listState.firstVisibleItemScrollOffset > 0 &&
                        listState.firstVisibleItemScrollOffset >= scrollWidth * 0.15) -> {
                    currentScreenIndex.intValue =
                        (currentScreenIndex.intValue + 1) % tutorialScreens.size
                }
            }
            scope.launch {
                listState.animateScrollToItem(currentScreenIndex.intValue)
            }
            return 0f
        }
    }

    BoxWithConstraints(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = flingBehaviour,
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(items = tutorialScreens) { _, screen ->
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .fillMaxHeight()
                        .width(this@BoxWithConstraints.maxWidth),
                    contentAlignment = Alignment.Center,
                ) {
                    screen()
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .width(tutorialScreens.size * 3 * 7.dp)
                .padding(bottom = 50.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(tutorialScreens.size) { index ->
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentScreenIndex.value == index) {
                                Color.Blue
                            } else {
                                Color.White
                            }
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = {
                scope.launch {
                    currentScreenIndex.intValue =
                        (currentScreenIndex.intValue + tutorialScreens.size - 1) % tutorialScreens.size
                    listState.animateScrollToItem(currentScreenIndex.intValue)
                }
            }) {
                Icon(
                    painter = painterResource(Res.drawable.left_arrow),
                    contentDescription = "to the left",
                    modifier = Modifier.alpha(0.5f),
                )
            }
            IconButton(
                onClick = {
                    scope.launch {
                        currentScreenIndex.intValue =
                            (currentScreenIndex.intValue + 1) % tutorialScreens.size
                        listState.animateScrollToItem(currentScreenIndex.intValue)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.right_arrow),
                    "to the right",
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}