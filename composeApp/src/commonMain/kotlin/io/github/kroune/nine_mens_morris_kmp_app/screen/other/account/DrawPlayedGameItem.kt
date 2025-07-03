package io.github.kroune.nine_mens_morris_kmp_app.screen.other.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.PastGamesUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.shimmerLoading
import kotlin.math.abs

@Composable
fun DrawPlayedGameItem(
    item: PastGamesUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(10.dp),
        shape = UiConstants.RoundedCornerShape2,
        onClick = onClick,
        colors = CardDefaults.cardColors(
//            contrainerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${(item.firstPlayerName.value as? LoginByIdApiResponses.Success)?.login}")
                Text("${(item.secondPlayerName.value as? LoginByIdApiResponses.Success)?.login}")
            }
            VerticalDivider()
            Text("${item.totalMoves}")
            VerticalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.padding1)
            ) {
                Row(
                    modifier = Modifier
                        .clip(UiConstants.RoundedCornerShape1)
                ) {
                    when (val playerRating = item.firstPlayerRating.value) {
                        is RatingByIdApiResponses.CredentialsError -> TODO()
                        is RatingByIdApiResponses.NetworkError -> TODO()
                        is RatingByIdApiResponses.ServerError -> TODO()
                        is RatingByIdApiResponses.Success -> {
                            val ratingDeltaText =
                                "${if (item.firstPlayerRatingDelta >= 0) "+" else "-"}${abs(item.firstPlayerRatingDelta)}"
                            val text = "${playerRating.rating} $ratingDeltaText"
                            Text(text)
                        }

                        is RatingByIdApiResponses.UnknownError -> TODO()
                        null -> {
                            Box(
                                modifier = Modifier
                                    .shimmerLoading()
                                    .clip(UiConstants.RoundedCornerShape1)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .clip(UiConstants.RoundedCornerShape1)
                ) {
                    when (val playerRating = item.secondPlayerRating.value) {
                        is RatingByIdApiResponses.CredentialsError -> TODO()
                        is RatingByIdApiResponses.NetworkError -> TODO()
                        is RatingByIdApiResponses.ServerError -> TODO()
                        is RatingByIdApiResponses.Success -> {
                            val ratingDeltaText =
                                "${if (item.secondPlayerRatingDelta >= 0) "+" else "-"}${abs(item.firstPlayerRatingDelta)}"
                            val text = "${playerRating.rating} $ratingDeltaText"
                            Text(text)
                        }

                        is RatingByIdApiResponses.UnknownError -> TODO()
                        null -> {
                            Box(
                                modifier = Modifier
                                    .shimmerLoading()
                                    .clip(UiConstants.RoundedCornerShape1)
                            )
                        }
                    }
                }
            }
        }
    }
}