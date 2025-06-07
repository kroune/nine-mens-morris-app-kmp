package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCount
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderUndoRedo
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
                    onEvent(GameWithFriendScreenEvent.OnPieceClick(it))
                },
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 5.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            RenderGameAnalyzeScreen(
                modifier = Modifier
                    .padding(
                        horizontal = 10.dp,
                    )
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f),
                positions = state.gameAnalyzePositions,
                depth = state.depth,
                onEvent = { onEvent(it) }
            )
            RenderUndoRedo(
                handleUndo = {
                    if (!state.gameEnded)
                        onEvent(GameWithFriendScreenEvent.Undo)
                },
                handleRedo = {
                    if (!state.gameEnded)
                        onEvent(GameWithFriendScreenEvent.Redo)
                }
            )
        }
    }
}
