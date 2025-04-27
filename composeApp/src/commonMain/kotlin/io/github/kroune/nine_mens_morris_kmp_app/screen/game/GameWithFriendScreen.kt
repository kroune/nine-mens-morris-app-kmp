package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import io.github.kroune.nine_mens_morris_kmp_app.common.collectValue
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithFriendEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp

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
            onDismiss = { gameEndPopUpClosed = true },
            onDiscarded = { gameEndPopUpClosed = true },
            onBackToMainScreen = {
                gameEndPopUpClosed = false
                component.onEvent(GameWithFriendEvent.Back)
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RenderPieceCount(pos = component.position.collectValue())
        LimitSize(
            0.8f
        ) {
            RenderGameBoard(
                modifier = Modifier,
                pos = component.position.collectValue(),
                selectedButton = component.selectedButton.collectValue(),
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
                    .fillMaxWidth(0.8f),
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
