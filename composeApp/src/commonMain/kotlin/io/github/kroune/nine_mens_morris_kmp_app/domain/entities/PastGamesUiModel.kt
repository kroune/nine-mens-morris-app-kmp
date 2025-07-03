package io.github.kroune.nine_mens_morris_kmp_app.domain.entities

import androidx.compose.runtime.MutableState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses

data class PastGamesUiModel(
    val gameId: Long,
    val firstPlayerId: Long,
    var firstPlayerName: MutableState<LoginByIdApiResponses?>,
    val firstPlayerRating: MutableState<RatingByIdApiResponses?>,
    val firstPlayerRatingDelta: Int,
    val secondPlayerId: Long,
    val secondPlayerName: MutableState<LoginByIdApiResponses?>,
    val secondPlayerRating: MutableState<RatingByIdApiResponses?>,
    val secondPlayerRatingDelta: Int,
    val gameDuration: Long,
    val totalMoves: Int,
)