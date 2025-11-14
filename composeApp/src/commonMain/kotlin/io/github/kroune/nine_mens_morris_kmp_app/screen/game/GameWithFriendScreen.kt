package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.MeasurePolicy
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderRedo
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderUndo
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.close
import org.jetbrains.compose.resources.painterResource

/**
 * Renders game with friend screen
 */
@Composable
fun GameWithFriendScreen(
    state: GameWithFriendScreenState,
    onEvent: (GameWithFriendScreenEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            IconButton(
                onClick = { onEvent(GameWithFriendScreenEvent.NavigateBack) },
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                    )
                ),
            ) {
                Icon(
                    painterResource(Res.drawable.close),
                    "close button",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                RenderPieceCountElement(
                    true,
                    state.position.pieceToMove,
                    state.position.freeGreenPieces,
                )
                RenderUndo {
                    if (!state.gameEnded)
                        onEvent(GameWithFriendScreenEvent.Undo)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RenderGameBoard(
                    modifier = Modifier.padding(top = 10.dp),
                    pos = state.position,
                    selectedButton = state.selectedButton,
                    moveHints = state.moveHints,
                    measurePolicy = MeasurePolicy.TAKE_MAX,
                    onClick = { onEvent(GameWithFriendScreenEvent.OnPieceClick(it)) },
                )
                RenderGameAnalyzeScreen(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    positions = state.gameAnalyzePositions,
                    depth = state.depth,
                    onEvent = { onEvent(it) }
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                RenderPieceCountElement(
                    false,
                    !state.position.pieceToMove,
                    state.position.freeBluePieces,
                )
                RenderRedo {
                    if (!state.gameEnded)
                        onEvent(GameWithFriendScreenEvent.Redo)
                }
            }
        }
    }
    var gameEndPopUpClosed by remember { mutableStateOf(false) }
    if (!gameEndPopUpClosed && state.gameEnded) {
        GameEndPopUp(
            onDismiss = { gameEndPopUpClosed = true },
            onDiscarded = { gameEndPopUpClosed = true },
            onBackToMainScreen = {
                gameEndPopUpClosed = false
                onEvent(GameWithFriendScreenEvent.NavigateBack)
            }
        )
    }
}
