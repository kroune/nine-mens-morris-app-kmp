package io.github.kroune.nine_mens_morris_kmp_app.domain.mappers

import androidx.compose.runtime.mutableStateOf
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PastGamesHistoryItem
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.PastGamesUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.CoroutineScope

fun PastGamesHistoryItem.toUiModel(
    accountInfoRepository: AccountInfoRepositoryI,
    scope: CoroutineScope
): PastGamesUiModel {
    val gameDuration = this.endTimestamp - this.startTimestamp
    val uiModel = PastGamesUiModel(
        this.gameId,
        this.firstPlayerId,
        mutableStateOf(null),
        mutableStateOf(null),
        this.firstPlayerRatingDelta,
        this.secondPlayerId,
        mutableStateOf(null),
        mutableStateOf(null),
        this.secondPlayerRatingDelta,
        gameDuration,
        this.totalMoves
    )
    AccountInfoUseCase(
        this.firstPlayerId,
        scope = scope,
        accountInfoRepository = accountInfoRepository,
        onLoginResult = {
            uiModel.firstPlayerName.value = it
        },
        onRatingResult = {
            uiModel.firstPlayerRating.value = it
        }
    )
    AccountInfoUseCase(
        this.secondPlayerId,
        scope = scope,
        accountInfoRepository = accountInfoRepository,
        onLoginResult = {
            uiModel.secondPlayerName.value = it
        },
        onRatingResult = {
            uiModel.secondPlayerRating.value = it
        }
    )
    return uiModel
}