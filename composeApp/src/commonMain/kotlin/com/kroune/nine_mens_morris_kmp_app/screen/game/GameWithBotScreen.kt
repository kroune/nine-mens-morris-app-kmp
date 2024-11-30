package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.GameWithBotEvent
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithBotScreen(
    component: GameWithBotScreenComponent
) {
    val onEvent: (GameWithBotEvent) -> Unit = {
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
                component.onEvent(GameWithBotEvent.Back)
            }
        )
    }
    RenderGameBoard(
        pos = component.position,
        selectedButton = component.selectedButton,
        moveHints = component.moveHints,
        onClick = {
            onEvent(GameWithBotEvent.OnPieceClick(it))
        }
    )
    RenderPieceCount(pos = component.position)
    RenderUndoRedo(
        handleUndo = {
            if (!component.gameEnded)
                onEvent(GameWithBotEvent.Undo)
        },
        handleRedo = {
            if (!component.gameEnded)
                onEvent(GameWithBotEvent.Redo)
        }
    )
}
