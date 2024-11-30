package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.GameWithFriendEvent
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
    RenderGameBoard(
        pos = component.position,
        selectedButton = component.selectedButton,
        moveHints = component.moveHints,
        onClick = {
            onEvent(GameWithFriendEvent.OnPieceClick(it))
        }
    )
    RenderPieceCount(pos = component.position)
    Box(
        modifier = Modifier
            .padding(0.dp, GAME_BOARD_BUTTON_WIDTH * 9.5f, 0.dp, 0.dp)
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        RenderGameAnalyzeScreen(
            positions = component.gameAnalyzePositions,
            depth = component.analyzeDepth,
            startAnalyze = { onEvent(GameWithFriendEvent.StartAnalyze) },
            increaseDepth = { onEvent(GameWithFriendEvent.IncreaseAnalyzeDepth) },
            decreaseDepth = { onEvent(GameWithFriendEvent.DecreaseAnalyzeDepth) }
        )
    }
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
