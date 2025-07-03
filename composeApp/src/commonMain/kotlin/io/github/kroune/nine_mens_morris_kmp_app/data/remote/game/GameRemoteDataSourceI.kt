package io.github.kroune.nine_mens_morris_kmp_app.data.remote.game

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PastGamesHistoryItem
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

interface GameRemoteDataSourceI {
    suspend fun connectToGame(
        gameId: Long,
        jwtToken: String,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>

    suspend fun searchForGame(
        channel: Channel<Long>,
        jwtToken: String
    ): SearchingForGameResponse

    suspend fun getPastGame(
        jwtToken: String,
        userId: Long,
        limit: Int,
        offset: Long,
    ): PastGamesApiResponse<List<PastGamesHistoryItem>>
}

class GameInfo(
    val isGreen: CompletableDeferred<Boolean>,
    val startPosition: CompletableDeferred<Position>,
    val enemyId: CompletableDeferred<Long>,
    val gameEnded: CompletableDeferred<Boolean>
)