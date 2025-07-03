package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PastGamesHistoryItem
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.game.GameInfo
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import kotlinx.coroutines.channels.Channel

interface GameRepositoryI {
    suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>

    suspend fun searchForGame(
        channel: Channel<Long>
    ): SearchingForGameResponse

    suspend fun getPastGames(
        accountId: Long,
        limit: Int,
        offset: Long
    ): PastGamesApiResponse<List<PastGamesHistoryItem>>
}