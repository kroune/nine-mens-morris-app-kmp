package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenState
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PlayerInfo
import io.github.kroune.nine_mens_morris_kmp_app.model.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(
    onEvent: (LeaderboardEvent) -> Unit,
    state: LeaderboardScreenState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.leaderboard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    fontWeight = FontWeight.W500,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            itemsIndexed(state.leaderboard) { index, player ->
                LeaderboardItem(
                    player = player,
                    onEvent = { onEvent(it) },
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
    player: PlayerInfo,
    onEvent: (LeaderboardEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    index: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape3,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DrawIcon(
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp),
                pictureByteArray = player.picture,
                onReload = {
                    onEvent(LeaderboardEvent.ReloadIcon(index))
                },
                onClick = {
                    onEvent(LeaderboardEvent.NavigateToAccountView(index))
                },
                snackbarHostState = snackbarHostState
            )
            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DrawName(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(20.dp),
                    onSuccess = @Composable {
                        Text(
                            it,
                            fontSize = 18.sp
                        )
                    },
                    accountName = player.loginResult,
                    onReload = { onEvent(LeaderboardEvent.ReloadName(index)) },
                    snackbarHostState = snackbarHostState
                )
                DrawRating(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(20.dp),
                    onSuccess = {
                        Text(
                            text = "${stringResource(Res.string.rating)}: $it",
                            fontWeight = FontWeight.W300
                        )
                    },
                    accountRating = player.ratingResult,
                    onReload = { onEvent(LeaderboardEvent.ReloadRating(index)) },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
