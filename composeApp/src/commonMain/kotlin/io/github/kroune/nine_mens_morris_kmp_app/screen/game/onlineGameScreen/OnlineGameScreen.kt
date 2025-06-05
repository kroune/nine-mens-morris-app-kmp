package io.github.kroune.nine_mens_morris_kmp_app.screen.game.onlineGameScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameScreenState
import io.github.kroune.nine_mens_morris_kmp_app.model.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.time_left
import org.jetbrains.compose.resources.stringResource

/**
 * renders online game screen
 */
@Composable
fun OnlineGameScreen(
    onEvent: (OnlineGameScreenEvent) -> Unit,
    state: OnlineGameScreenState
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .heightIn(max = 150.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    PlayerCard(
                        playerName = state.ownAccountLoginResult,
                        pictureByteArray = state.ownAccountPictureResult,
                        isGreen = state.isGreen,
                        rating = state.ownAccountRatingResult,
                        pos = state.position,
                        snackbarHostState = snackbarHostState,
                        onEvent = { onEvent(it) },
                        ownAccount = true
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    PlayerCard(
                        playerName = state.enemyAccountLoginResult,
                        pictureByteArray = state.enemyAccountPictureResult,
                        isGreen = !state.isGreen,
                        rating = state.enemyAccountRatingResult,
                        pos = state.position,
                        snackbarHostState = snackbarHostState,
                        onEvent = { onEvent(it) },
                        ownAccount = false
                    )
                }
            }
            Text(
                text = "${stringResource(Res.string.time_left)}: ${state.timeLeft}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            var showGameEndDialog by remember { mutableStateOf(true) }
            LimitSize(
                0.8f
            ) {
                RenderGameBoard(
                    modifier = Modifier,
                    pos = state.position,
                    selectedButton = state.selectedButton,
                    moveHints = state.moveHints,
                    onClick = { onEvent(OnlineGameScreenEvent.Click(it)) }
                )
            }
            if (!state.gameEnded) {
                if (state.displayGiveUpConfirmation)
                    GiveUpConfirmation(
                        onGiveUpDiscarded = {
                            onEvent(OnlineGameScreenEvent.GiveUpDiscarded)
                        },
                        onGiveUp = {
                            onEvent(OnlineGameScreenEvent.GiveUp)
                        }
                    )
            } else {
                if (showGameEndDialog) {
                    GameEndPopUp(
                        {
                            showGameEndDialog = false
                        },
                        {
                            showGameEndDialog = false
                        },
                        {
                            onEvent(OnlineGameScreenEvent.NavigateToMainScreen)
                        }
                    )
                }
            }
        }
    }
}
