package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.onlineGameRepository
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame.GameInfo
import kotlinx.coroutines.channels.Channel

class OnlineGameInteractorImpl : OnlineGameInteractorI {
    val repository = onlineGameRepository

    override suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        return repository.connect(
            gameId,
            jwtTokenInteractor.getJwtToken()!!,
            channelToSendMoves,
            channelToReceiveMoves
        )
    }
}