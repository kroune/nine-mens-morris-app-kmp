package com.kroune.nine_mens_morris_kmp_app.interactors.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameInfo
import kotlinx.coroutines.channels.Channel

interface OnlineGameInteractorI {
    suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>
}