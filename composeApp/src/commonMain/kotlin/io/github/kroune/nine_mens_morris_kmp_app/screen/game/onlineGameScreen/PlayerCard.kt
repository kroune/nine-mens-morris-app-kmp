package io.github.kroune.nine_mens_morris_kmp_app.screen.game.onlineGameScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawRating
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderSmallPieceCountElement

@Composable
fun PlayerCard(
    playerName: LoginByIdApiResponses?,
    pictureByteArray: AccountPictureByIdApiResponses?,
    isGreen: Boolean,
    rating: RatingByIdApiResponses?,
    pos: Position,
    snackbarHostState: SnackbarHostState,
    onEvent: (OnlineGameScreenEvent) -> Unit,
    ownAccount: Boolean,
) {
    Card(
        Modifier.padding(10.dp)
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
                onReload = { onEvent(OnlineGameScreenEvent.ReloadIcon(ownAccount)) },
                onClick = {
                    val event = if (ownAccount) {
                        OnlineGameScreenEvent.NavigateToOwnAccountView
                    } else {
                        OnlineGameScreenEvent.NavigateToAccountView
                    }
                    onEvent(event)
                },
                snackbarHostState = snackbarHostState
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
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
                    snackbarHostState = snackbarHostState
                )

                val shouldMove = when {
                    isGreen && pos.pieceToMove -> true
                    !isGreen && !pos.pieceToMove -> true
                    else -> false
                }
                RenderSmallPieceCountElement(
                    isGreen,
                    shouldMove,
                    if (isGreen) pos.freeGreenPieces else pos.freeBluePieces,
                )
                DrawRating(
                    accountRating = rating,
                    onReload = { onEvent(OnlineGameScreenEvent.ReloadRating(ownAccount)) },
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}