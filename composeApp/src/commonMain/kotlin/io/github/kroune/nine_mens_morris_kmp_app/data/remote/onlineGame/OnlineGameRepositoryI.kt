package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

interface OnlineGameRepositoryI {
    suspend fun connect(
        gameId: Long,
        jwtToken: String,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>

    suspend fun giveUp(
        gameId: Long,
        jwtToken: String,
    ): GiveUpApiResponse

    suspend fun sendMove(
        move: Movement,
        gameId: Long,
        jwtToken: String
    ): SendMoveApiResponse
}

class GameInfo(
    val isGreen: CompletableDeferred<Boolean>,
    val startPosition: CompletableDeferred<Position>,
    val enemyId: CompletableDeferred<Long>,
    val gameEnded: CompletableDeferred<Boolean>
)