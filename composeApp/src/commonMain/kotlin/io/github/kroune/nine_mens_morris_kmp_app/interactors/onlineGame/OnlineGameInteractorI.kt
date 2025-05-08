package io.github.kroune.nine_mens_morris_kmp_app.interactors.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameInfo
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import kotlinx.coroutines.channels.Channel

interface OnlineGameInteractorI {
    suspend fun connect(
        gameId: Long,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit>

    suspend fun giveUp(
        gameId: Long
    ): GiveUpApiResponse

    suspend fun sendMove(
        move: Movement,
        gameId: Long,
    ): SendMoveApiResponse
}