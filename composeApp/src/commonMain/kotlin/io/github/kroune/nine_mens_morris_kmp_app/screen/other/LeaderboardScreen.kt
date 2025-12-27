package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderBoardPlayerInfo
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawRating
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.close
import ninemensmorrisappkmp.composeapp.generated.resources.leaderboard
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(
    onEvent: (LeaderboardEvent) -> Unit,
    state: LeaderboardScreenState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(),
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 10.dp)
                .fillMaxSize(),
            contentPadding = WindowInsets.safeContent.asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.leaderboard),
                        modifier = Modifier
                            .padding(top = 10.dp),
                        fontWeight = FontWeight.W500,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    IconButton(
                        { onEvent(LeaderboardEvent.Back) },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.close),
                            contentDescription = "close button",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            itemsIndexed(state.leaderboard) { index, player ->
                sharedTransitionScope.LeaderboardItem(
                    player = player,
                    onEvent = { onEvent(it) },
                    snackbarHostState,
                    index,
                    animatedVisibilityScope
                )
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.LeaderboardItem(
    player: LeaderBoardPlayerInfo,
    onEvent: (LeaderboardEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    index: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape3,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DrawIcon(
                modifier = Modifier
                    .then(
                        if (player.accountId != null) {
                            Modifier.sharedElement(
                                rememberSharedContentState(key = "icon-${player.accountId}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        } else
                            Modifier
                    )
                    .size(80.dp)
                    .padding(10.dp),
                pictureByteArray = player.picture,
                onReload = { onEvent(LeaderboardEvent.ReloadIcon(index)) },
                onClick = { onEvent(LeaderboardEvent.NavigateToAccountView(index)) },
                snackbarHostState = snackbarHostState
            )

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DrawName(
                    modifier = Modifier
                        .then(
                            if (player.accountId != null) {
                                Modifier.sharedElement(
                                    rememberSharedContentState(key = "name-${player.accountId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                Modifier
                            }
                        )
                        .fillMaxWidth()
                        .heightIn(20.dp),
                    onSuccess = {
                        Text(
                            it,
                            fontSize = 18.sp,
                        )
                    },
                    accountName = player.loginResult,
                    onReload = { onEvent(LeaderboardEvent.ReloadName(index)) },
                    snackbarHostState = snackbarHostState
                )
                DrawRating(
                    modifier = Modifier
                        .then(
                            if (player.accountId != null) {
                                Modifier.sharedElement(
                                    rememberSharedContentState(key = "rating-${player.accountId}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                Modifier
                            }
                        )
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
