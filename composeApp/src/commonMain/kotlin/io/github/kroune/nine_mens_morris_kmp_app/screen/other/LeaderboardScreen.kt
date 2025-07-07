package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderBoardPlayerInfo
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.LocalNavAnimatedVisibilityScope
import io.github.kroune.nine_mens_morris_kmp_app.screen.LocalSharedTransitionScope
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.CustomDrawRating
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(
    state: LeaderboardScreenState,
    onEvent: (LeaderboardEvent) -> Unit,
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
                    snackbarHostState = snackbarHostState,
                    index = index,
                )
            }
        }
    }
}

/**
 * Draws a single item in the leaderboard column
 */
@Composable
inline fun LeaderboardItem(
    player: LeaderBoardPlayerInfo?,
    crossinline onEvent: (LeaderboardEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    index: Int,
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
            with(LocalSharedTransitionScope.current!!) {
                DrawIcon(
                    modifier = Modifier
                        .then(
                            if (player?.accountId != null) {
                                val key =
                                    rememberSharedContentState(key = "icon-${player.accountId}")
                                Modifier
                                    .sharedElement(
                                        key,
                                        animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                    )
                            } else
                                Modifier
                        )
                        .size(80.dp)
                        .padding(10.dp),
                    pictureByteArray = player?.picture?.value,
                    onReload = {
                        onEvent(LeaderboardEvent.ReloadIcon(index))
                    },
                    onClick = {
                        onEvent(LeaderboardEvent.NavigateToAccountView(index))
                    },
                    snackbarHostState = snackbarHostState
                )
            }
            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                with(LocalSharedTransitionScope.current!!) {
                    DrawName(
                        modifier = Modifier
                            .then(
                                if (player?.accountId != null) {
                                    val key =
                                        rememberSharedContentState(key = "name-${player.accountId}")
                                    Modifier
                                        .sharedElement(
                                            key,
                                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                        )
                                } else
                                    Modifier
                            ),
                        onSuccess = {
                            Text(
                                it,
                                maxLines = 1,
                                fontSize = 18.sp
                            )
                        },
                        accountName = player?.loginResult?.value,
                        onReload = { onEvent(LeaderboardEvent.ReloadName(index)) },
                        placeholderStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        placeholderText = "some random name 123",
                        snackbarHostState = snackbarHostState
                    )
                }
                with(LocalSharedTransitionScope.current!!) {
                    CustomDrawRating(
                        modifier = Modifier
                            .then(
                                if (player?.accountId != null) {
                                    val key =
                                        rememberSharedContentState(key = "rating-${player.accountId}")
                                    Modifier
                                        .sharedElement(
                                            key,
                                            animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current!!
                                        )
                                } else
                                    Modifier
                            ),
                        onSuccess = {
                            Text(
                                text = "${stringResource(Res.string.rating)}: $it",
                                maxLines = 1,
                                fontWeight = FontWeight.W300
                            )
                        },
                        accountRating = player?.ratingResult?.value,
                        onReload = { onEvent(LeaderboardEvent.ReloadRating(index)) },
                        placeholderStyle = TextStyle(
                            fontWeight = FontWeight.W500
                        ),
                        placeholderText = "${stringResource(Res.string.rating)}: 12345",
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}
