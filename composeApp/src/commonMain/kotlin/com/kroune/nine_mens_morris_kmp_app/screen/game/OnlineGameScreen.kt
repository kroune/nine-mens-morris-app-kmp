package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import com.kroune.nine_mens_morris_kmp_app.event.OnlineGameScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import com.kroune.nine_mens_morris_kmp_app.screen.DrawName
import com.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.GameEndPopUp
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
    component: OnlineGameComponent
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backgroundColor = Color.Transparent
    ) { _ ->
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .heightIn(max = 110.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    PlayerCard(
                        playerName = component.ownAccountName,
                        pictureByteArray = component.ownPictureByteArray,
                        isGreen = component.isGreen,
                        rating = component.ownAccountRating,
                        pos = component.position,
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        onEvent = { component.onEvent(it) },
                        ownAccount = true
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    PlayerCard(
                        playerName = component.enemyAccountName,
                        pictureByteArray = component.enemyPictureByteArray,
                        isGreen = !component.isGreen,
                        rating = component.enemyAccountRating,
                        pos = component.position,
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        onEvent = { component.onEvent(it) },
                        ownAccount = true
                    )
                }
            }
            TurnTimerUI(component.timeLeft)

            if (component.displayGiveUpConfirmation.value && !component.gameEnded) {
                GiveUpConfirm(
                    onGiveUpDiscarded = {
                        component.onEvent(OnlineGameScreenEvent.GiveUpDiscarded)
                    },
                    onGiveUp = {
                        component.onEvent(OnlineGameScreenEvent.GiveUp)
                    }
                )
            }
            var showGameEndDialog by remember { mutableStateOf(true) }
            if (!component.gameEnded) {
                // reset showing status to default
                showGameEndDialog = true
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
                            component.onEvent(OnlineGameScreenEvent.NavigateToMainScreen)
                        }
                    )
                }
            }
            RenderGameBoard(
                pos = component.position,
                selectedButton = component.selectedButton,
                moveHints = component.moveHints,
                onClick = { component.onEvent(OnlineGameScreenEvent.Click(it)) }
            )
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
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onGiveUpDiscarded()
                }) {
                    Text(stringResource(Res.string.no))
                }
                Button(onClick = {
                    onGiveUp()
                }) {
                    Text(stringResource(Res.string.yes))
                }
            }
        },
        backgroundColor = Color.Gray
    )
}

@Composable
fun TurnTimerUI(timeLeft: Int) {
    Box(
        modifier = Modifier
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
    ) {
        Text(
            text = "${stringResource(Res.string.time_left)}: $timeLeft",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun PlayerCard(
    playerName: Result<String>?,
    pictureByteArray: Result<ByteArray>?,
    isGreen: Boolean,
    rating: Result<Long>?,
    pos: Position,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onEvent: (OnlineGameScreenEvent) -> Unit,
    ownAccount: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
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
            scope = scope,
            snackbarHostState = snackbarHostState
        )
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier
                .height(with(LocalDensity.current) { 20.sp.toDp() })
            ) {
                DrawName(
                    text = @Composable {
                        Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    accountName = playerName,
                    onReload = { onEvent(OnlineGameScreenEvent.ReloadName(ownAccount)) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier =
                Modifier.requiredSize(
                    (30 * when {
                        isGreen && pos.pieceToMove -> 1.5f
                        !isGreen && !pos.pieceToMove -> 1.5f
                        else -> 1f
                    }).dp
                )
                    .aspectRatio(1f)
                    .background(if (isGreen) Color.Green else Color.Blue, CircleShape)
                    .alpha(if (pos.freeGreenPieces == 0.toUByte()) 0f else 1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    color = if (!isGreen) {
                        Color.Green
                    } else {
                        Color.Blue
                    },
                    text = if (!isGreen) {
                        pos.freeBluePieces.toString()
                    } else {
                        pos.freeGreenPieces.toString()
                    },
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            DrawRating(
                {
                    Text(it.toString())
                },
                rating,
                { onEvent(OnlineGameScreenEvent.ReloadRating(ownAccount)) },
                scope,
                snackbarHostState
            )
        }
    }
}
