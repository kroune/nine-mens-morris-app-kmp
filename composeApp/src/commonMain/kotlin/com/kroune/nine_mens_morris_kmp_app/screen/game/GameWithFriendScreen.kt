package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.GameWithFriendEvent
import com.kroune.nine_mens_morris_kmp_app.getScreenDpSize
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
    Column(modifier = Modifier.height(getScreenDpSize().height)) {
        Box {
            RenderGameBoard(
                pos = component.position,
                selectedButton = component.selectedButton,
                moveHints = component.moveHints,
                onClick = {
                    onEvent(GameWithFriendEvent.OnPieceClick(it))
                }
            )
            RenderPieceCount(pos = component.position)
        }
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            RenderGameAnalyzeScreen(
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
