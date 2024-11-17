package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

import androidx.compose.runtime.MutableState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame.GameInfo
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.channels.Channel

interface OnlineGameInteractorI {
    suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>
}