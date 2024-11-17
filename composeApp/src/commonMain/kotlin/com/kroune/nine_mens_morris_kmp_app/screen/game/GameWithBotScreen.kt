package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.runtime.Composable
import com.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.GameWithBotEvent

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
        handleUndo = { onEvent(GameWithBotEvent.Undo) },
        handleRedo = { onEvent(GameWithBotEvent.Redo) }
    )
}
