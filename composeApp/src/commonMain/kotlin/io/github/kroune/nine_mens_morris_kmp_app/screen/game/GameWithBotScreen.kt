package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.GameWithBotScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderRedo
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderUndo
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithBotScreen(
    state: GameWithBotScreenState,
    onEvent: (GameWithBotScreenEvent) -> Unit
) {
    var gameEndPopUpClosed by rememberSaveable { mutableStateOf(false) }
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
                onEvent(GameWithBotScreenEvent.Back)
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RenderPieceCountElement(
                true,
                state.position.pieceToMove,
                state.position.freeGreenPieces
            )
            RenderUndo(
                {
                    if (!state.gameEnded)
                        onEvent(GameWithBotScreenEvent.Undo)
                }
            )
        }
        RenderGameBoard(
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f),
            pos = state.position,
            selectedButton = state.selectedButton,
            moveHints = state.moveHints,
            onClick = {
                onEvent(GameWithBotScreenEvent.OnPieceClick(it))
            }
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RenderPieceCountElement(
                false,
                !state.position.pieceToMove,
                state.position.freeBluePieces
            )
            RenderRedo(
                {
                    if (!state.gameEnded)
                        onEvent(GameWithBotScreenEvent.Redo)
                }
            )
        }
    }
}
