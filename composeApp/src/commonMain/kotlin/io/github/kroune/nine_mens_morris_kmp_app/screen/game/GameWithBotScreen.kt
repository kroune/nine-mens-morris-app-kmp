package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenState
import io.github.kroune.nine_mens_morris_kmp_app.model.event.game.GameWithBotEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithBotScreen(
    state: GameWithBotScreenState,
    onEvent: (GameWithBotEvent) -> Unit
) {
    var gameEndPopUpClosed by remember { mutableStateOf(false) }
    if (!gameEndPopUpClosed && state.gameEnded) {
        GameEndPopUp(
            {
                gameEndPopUpClosed = true
            },
            {
                gameEndPopUpClosed = true
            },
            {
                gameEndPopUpClosed = false
                onEvent(GameWithBotEvent.Back)
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RenderPieceCount(pos = state.position)
        LimitSize(
            0.8f
        ) {
            RenderGameBoard(
                modifier = Modifier,
                pos = state.position,
                selectedButton = state.selectedButton,
                moveHints = state.moveHints,
                onClick = {
                    onEvent(GameWithBotEvent.OnPieceClick(it))
                }
            )
        }
        RenderUndoRedo(
            handleUndo = {
                if (!state.gameEnded)
                    onEvent(GameWithBotEvent.Undo)
            },
            handleRedo = {
                if (!state.gameEnded)
                    onEvent(GameWithBotEvent.Redo)
            }
        )
    }
}
