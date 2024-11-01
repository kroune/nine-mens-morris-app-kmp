package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.essenty.backhandler.BackCallback
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.common.AppTheme
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import com.kroune.nine_mens_morris_kmp_app.component.OnlineGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.OnlineGameScreenEvent
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.baseline_account_circle_48
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource

/**
 * renders online game screen
 */
@Composable
fun OnlineGameScreen(
    component: OnlineGameScreenComponent
) {
    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayersUI(
                pos = component.position,
                timeLeft = component.timeLeft,
                isGreen = component.isGreen,
                ownAccountName = component.ownAccountName,
                ownPictureByteArray = component.ownPictureByteArray,
                ownAccountRating = component.ownAccountRating,
                enemyAccountName = component.enemyAccountName,
                enemyPictureByteArray = component.enemyPictureByteArray,
                enemyAccountRating = component.enemyAccountRating,
            )

            if (displayGiveUpConfirmation.value) {
                GiveUpConfirm(
                    onGiveUp = {
                        component.onEvent(OnlineGameScreenEvent.GiveUp)
                    }
                )
            }
            if (!component.gameEnded) {
                BackCallback() {
                    displayGiveUpConfirmation.value = true
                }
            }
            RenderGameBoard(
                pos = component.position,
                selectedButton = component.selectedButton,
                moveHints = component.moveHints,
                onClick = { component.onEvent(OnlineGameScreenEvent.Click(it)) }
            )
//            RenderUndoRedo(handleUndo = handleUndo, handleRedo = handleRedo)
        }
    }
}

private val displayGiveUpConfirmation = mutableStateOf(false)

@Composable
private fun GiveUpConfirm(
    onGiveUp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), contentAlignment = Alignment.Center
    ) {
        Column {
            Text("Are you sure you want to give up?")
            Button(onClick = {
                onGiveUp()
            }) {
                Text("Yes")
            }
            Button(onClick = {
                displayGiveUpConfirmation.value = false
            }) {
                Text("No")
            }
        }
    }
}

@Composable
fun TurnTimerUI(timeLeft: Int) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "Time left: $timeLeft s",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PlayerCard(
    playerName: Result<String>?,
    pictureByteArray: Result<ByteArray>?,
    isGreen: Boolean,
    rating: Result<Long>?,
    pos: Position
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        when {
            pictureByteArray == null -> {
                Icon(
                    painter = painterResource(Res.drawable.baseline_account_circle_48),
                    contentDescription = "profile loading icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }

            pictureByteArray.isSuccess -> {
                Image(
                    bitmap = pictureByteArray.getOrThrow().decodeToImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray, shape = CircleShape)
                )
            }

            pictureByteArray.isFailure -> {
                Text("error loading profile picture ${pictureByteArray.exceptionOrNull()!!.message}")
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            when {
                playerName == null -> {
                    Text(text = "loading info...", fontSize = 16.sp)
                }

                playerName.isSuccess -> {
                    Text(text = playerName.getOrThrow(), fontSize = 16.sp)
                }

                playerName.isFailure -> {
                    Text("error loading profile name ${playerName.exceptionOrNull()!!.message}")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier =
                Modifier.size(
                    (GAME_BOARD_BUTTON_WIDTH.value * when {
                        isGreen && pos.pieceToMove -> 1.5f
                        !isGreen && !pos.pieceToMove -> 1.5f
                        else -> 1f
                    }).dp
                )
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

            Spacer(modifier = Modifier.height(4.dp))
            when {
                rating == null -> {
                    Text(text = "loading info...", fontSize = 16.sp)
                }

                rating.isSuccess -> {
                    Text(text = rating.getOrThrow().toString(), fontSize = 16.sp)
                }

                rating.isFailure -> {
                    Text("error loading profile rating ${rating.exceptionOrNull()!!.message}")
                }
            }
        }
    }
}

@Composable
fun PlayersUI(
    pos: Position,
    timeLeft: Int,
    isGreen: Boolean?,
    ownAccountName: Result<String>?,
    ownPictureByteArray: Result<ByteArray>?,
    ownAccountRating: Result<Long>?,
    enemyAccountName: Result<String>?,
    enemyPictureByteArray: Result<ByteArray>?,
    enemyAccountRating: Result<Long>?,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerCard(
                playerName = ownAccountName,
                pictureByteArray = ownPictureByteArray,
                isGreen = isGreen == true,
                rating = ownAccountRating,
                pos = pos,
            )

            Spacer(modifier = Modifier.width(16.dp))
            PlayerCard(
                playerName = enemyAccountName,
                pictureByteArray = enemyPictureByteArray,
                isGreen = isGreen == false,
                rating = enemyAccountRating,
                pos = pos,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TurnTimerUI(timeLeft)
    }
}

