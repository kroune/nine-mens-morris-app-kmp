package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import com.kroune.nine_mens_morris_kmp_app.component.GameWithFriendScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.GameWithFriendEvent

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
    RenderGameBoard(
        pos = component.position,
        selectedButton = component.selectedButton,
        moveHints = component.moveHints,
        onClick = {
            onEvent(GameWithFriendEvent.OnPieceClick(it))
        }
    )
    RenderPieceCount(pos = component.position)
    RenderUndoRedo(
        handleUndo = { onEvent(GameWithFriendEvent.Undo) },
        handleRedo = { onEvent(GameWithFriendEvent.Redo) }
    )
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
}
