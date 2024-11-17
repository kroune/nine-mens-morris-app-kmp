package com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

typealias GameInfo = Triple<CompletableDeferred<Boolean>, CompletableDeferred<Position>, CompletableDeferred<Long>>
sealed interface OnlineGameRepositoryI {
    suspend fun connect(
        gameId: Long,
        jwtToken: String,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>
}