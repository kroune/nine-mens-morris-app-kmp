package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.DrawIcon
import com.kroune.nine_mens_morris_kmp_app.common.DrawName
import com.kroune.nine_mens_morris_kmp_app.common.DrawRating
import com.kroune.nine_mens_morris_kmp_app.component.LeaderboardComponent
import com.kroune.nine_mens_morris_kmp_app.component.Player
import com.kroune.nine_mens_morris_kmp_app.event.LeaderboardEvent
import kotlinx.coroutines.CoroutineScope
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(component: LeaderboardComponent) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backgroundColor = Color.Transparent
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            stickyHeader {
                Text(
                    text = stringResource(Res.string.leaderboard),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(8.dp)
                )
            }

            itemsIndexed(component.players) { index, player ->
                LeaderboardItem(
                    player = player,
                    onEvent = { component.onEvent(it) },
                    scope,
                    snackbarHostState,
                    index
                )
            }
        }
    }
}

/**
 * Draws a single item in the leaderboard column
 */
@Composable
fun LeaderboardItem(
    player: Player,
    onEvent: (LeaderboardEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    index: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(3.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
        backgroundColor = Color.LightGray
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DrawIcon(
                modifier = Modifier
                    .sizeIn(maxWidth = 80.dp, maxHeight = 80.dp),
                player.pictureByteArray.value,
                {
                    onEvent(LeaderboardEvent.ReloadIcon(index))
                },
                scope,
                snackbarHostState
            )
            Column(verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.height(40.dp)) {
                    DrawName(
                        { it },
                        player.accountName.value,
                        { onEvent(LeaderboardEvent.ReloadName(index)) },
                        scope,
                        snackbarHostState
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Box(modifier = Modifier.height(40.dp)) {
                    DrawRating(
                        {
                            Text(
                                text = "${stringResource(Res.string.rating)}: $it",
                                color = Color.Gray
                            )
                        },
                        player.accountRating.value,
                        { onEvent(LeaderboardEvent.ReloadRating(index)) },
                        scope,
                        snackbarHostState
                    )
                }
            }
        }
    }
}
