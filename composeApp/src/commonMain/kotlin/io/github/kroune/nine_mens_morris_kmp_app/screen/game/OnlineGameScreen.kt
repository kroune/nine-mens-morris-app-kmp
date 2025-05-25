package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameScreenState
import io.github.kroune.nine_mens_morris_kmp_app.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import io.github.kroune.nine_mens_morris_kmp_app.screen.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp
import kotlinx.coroutines.CoroutineScope
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.no
import ninemensmorrisappkmp.composeapp.generated.resources.time_left
import ninemensmorrisappkmp.composeapp.generated.resources.want_to_give_up
import ninemensmorrisappkmp.composeapp.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

/**
 * renders online game screen
 */
@Composable
fun OnlineGameScreen(
    onEvent: (OnlineGameScreenEvent) -> Unit,
    state: OnlineGameScreenState
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { _ ->
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            with(state) {
                Row(
                    modifier = Modifier
                        .heightIn(max = 150.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PlayerCard(
                            playerName = ownAccountLoginResult,
                            pictureByteArray = ownAccountPictureResult,
                            isGreen = isGreen,
                            rating = ownAccountRatingResult,
                            pos = position,
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            onEvent = { onEvent(it) },
                            ownAccount = true
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PlayerCard(
                            playerName = enemyAccountLoginResult,
                            pictureByteArray = enemyAccountPictureResult,
                            isGreen = !isGreen,
                            rating = enemyAccountRatingResult,
                            pos = position,
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            onEvent = { onEvent(it) },
                            ownAccount = false
                        )
                    }
                }
                Text(
                    text = "${stringResource(Res.string.time_left)}: $timeLeft",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                var showGameEndDialog by remember { mutableStateOf(true) }
                LimitSize(
                    0.8f
                ) {
                    RenderGameBoard(
                        modifier = Modifier,
                        pos = position,
                        selectedButton = selectedButton,
                        moveHints = moveHints,
                        onClick = { onEvent(OnlineGameScreenEvent.Click(it)) }
                    )
                }
                if (!gameEnded) {
                    if (displayGiveUpConfirmation)
                        GiveUpConfirm(
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
}

@Composable
private fun GiveUpConfirm(
    onGiveUpDiscarded: () -> Unit,
    onGiveUp: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onGiveUpDiscarded()
        },
        title = {
            Text(stringResource(Res.string.want_to_give_up))
        },
        confirmButton = {
            Button(
                onClick = {
                    onGiveUp()
                }
            ) {
                Text(stringResource(Res.string.yes))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onGiveUpDiscarded()
                }
            ) {
                Text(stringResource(Res.string.no))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}


@Composable
fun PlayerCard(
    playerName: LoginByIdApiResponses?,
    pictureByteArray: AccountPictureByIdApiResponses?,
    isGreen: Boolean,
    rating: RatingByIdApiResponses?,
    pos: Position,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onEvent: (OnlineGameScreenEvent) -> Unit,
    ownAccount: Boolean
) {
    Card(
        Modifier
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
                .fillMaxSize()
        ) {
            DrawIcon(
                modifier = Modifier
                    .padding(5.dp)
                    .aspectRatio(1f),
                pictureByteArray = pictureByteArray,
                onReload = {
                    onEvent(OnlineGameScreenEvent.ReloadIcon(ownAccount))
                },
                onClick = {
                    if (ownAccount)
                        onEvent(OnlineGameScreenEvent.NavigateToOwnAccountView)
                    else
                        onEvent(OnlineGameScreenEvent.NavigateToAccountView)
                },
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(with(LocalDensity.current) { 20.sp.toDp() })
                ) {
                    DrawName(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        onSuccess = {
                            Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        accountName = playerName,
                        onReload = { onEvent(OnlineGameScreenEvent.ReloadName(ownAccount)) },
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                val shouldMove = when {
                    isGreen && pos.pieceToMove -> true
                    !isGreen && !pos.pieceToMove -> true
                    else -> false
                }
                RenderPieceCountElement(
                    isGreen,
                    shouldMove,
                    if (isGreen) pos.freeGreenPieces else pos.freeBluePieces
                )
                Spacer(modifier = Modifier.height(5.dp))
                DrawRating(
                    onSuccess = @Composable {
                        Text(it.toString())
                    },
                    accountRating = rating,
                    onReload = { onEvent(OnlineGameScreenEvent.ReloadRating(ownAccount)) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
