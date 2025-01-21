package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.game.GameWithFriendEvent
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithFriendScreen(
    component: GameWithFriendScreenComponent
) {
    val onEvent: (GameWithFriendEvent) -> Unit = {
        component.onEvent(it)
    }
    var gameEndPopUpClosed by remember { mutableStateOf(false) }
    if (!gameEndPopUpClosed && component.gameEnded) {
        GameEndPopUp(
            {
                gameEndPopUpClosed = true
            },
            {
                gameEndPopUpClosed = true
            },
            {
                gameEndPopUpClosed = false
                component.onEvent(GameWithFriendEvent.Back)
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.TopCenter
        ) {
            RenderPieceCount(pos = component.position)
            val heightBigger = derivedStateOf { maxHeight > maxWidth }
            RenderGameBoard(
                modifier = Modifier
                    // FIXME: this is some garbage
                    .then(
                        if (!heightBigger.value)
                            Modifier.fillMaxHeight(0.6f)
                        else
                            Modifier.fillMaxWidth(0.6f)
                    ),
                pos = component.position,
                selectedButton = component.selectedButton,
                moveHints = component.moveHints,
                onClick = {
                    onEvent(GameWithFriendEvent.OnPieceClick(it))
                },
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(contentAlignment = Alignment.TopCenter) {
            RenderGameAnalyzeScreen(
                modifier = Modifier
                    .padding(
                        start = GAME_BOARD_BUTTON_WIDTH,
                        end = GAME_BOARD_BUTTON_WIDTH
                    )
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f),
                positions = component.gameAnalyzePositions,
                depth = component.analyzeDepth,
                startAnalyze = { onEvent(GameWithFriendEvent.StartAnalyze) },
                increaseDepth = { onEvent(GameWithFriendEvent.IncreaseAnalyzeDepth) },
                decreaseDepth = { onEvent(GameWithFriendEvent.DecreaseAnalyzeDepth) }
            )
            RenderUndoRedo(
                handleUndo = {
                    if (!component.gameEnded)
                        onEvent(GameWithFriendEvent.Undo)
                },
                handleRedo = {
                    if (!component.gameEnded)
                        onEvent(GameWithFriendEvent.Redo)
                }
            )
        }
    }
}
