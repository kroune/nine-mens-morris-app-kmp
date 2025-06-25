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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.GameWithFriendScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderRedo
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderUndo
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithFriendScreen(
    state: GameWithFriendScreenState,
    onEvent: (GameWithFriendScreenEvent) -> Unit
) {
    var gameEndPopUpClosed by remember { mutableStateOf(false) }
    if (!gameEndPopUpClosed && state.gameEnded) {
        GameEndPopUp(
            onDismiss = { gameEndPopUpClosed = true },
            onDiscarded = { gameEndPopUpClosed = true },
            onBackToMainScreen = {
                gameEndPopUpClosed = false
                onEvent(GameWithFriendScreenEvent.Back)
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
                        onEvent(GameWithFriendScreenEvent.Undo)
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RenderGameBoard(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .weight(1f),
                pos = state.position,
                selectedButton = state.selectedButton,
                moveHints = state.moveHints,
                onClick = {
                    onEvent(GameWithFriendScreenEvent.OnPieceClick(it))
                },
            )
            RenderGameAnalyzeScreen(
                modifier = Modifier
                    .padding(
                        horizontal = 10.dp, vertical = 10.dp
                    ),
                positions = state.gameAnalyzePositions,
                depth = state.depth,
                onEvent = { onEvent(it) }
            )
        }
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
                        onEvent(GameWithFriendScreenEvent.Redo)
                }
            )
        }
    }
}
