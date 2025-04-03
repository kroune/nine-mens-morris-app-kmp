package io.github.kroune.nine_mens_morris_kmp_app.screen.other

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardComponent
import io.github.kroune.nine_mens_morris_kmp_app.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
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
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(Res.string.leaderboard),
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
    player: AccountInfoUseCase.PlayerInfo,
    onEvent: (LeaderboardEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    index: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(3.dp, MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DrawIcon(
                modifier = Modifier
                    .sizeIn(maxWidth = 80.dp, maxHeight = 80.dp),
                pictureByteArray = player.accountPicture.value,
                onReload = {
                    onEvent(LeaderboardEvent.ReloadIcon(index))
                },
                onClick = {
                    onEvent(LeaderboardEvent.NavigateToAccountView(index))
                },
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.height(40.dp)) {
                    DrawName(
                        text = @Composable { Text(it) },
                        accountName = player.name.value,
                        onReload = { onEvent(LeaderboardEvent.ReloadName(index)) },
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Box(modifier = Modifier.height(40.dp)) {
                    DrawRating(
                        {
                            Text(
                                text = "${stringResource(Res.string.rating)}: $it"
                            )
                        },
                        player.rating.value,
                        { onEvent(LeaderboardEvent.ReloadRating(index)) },
                        scope,
                        snackbarHostState
                    )
                }
            }
        }
    }
}
