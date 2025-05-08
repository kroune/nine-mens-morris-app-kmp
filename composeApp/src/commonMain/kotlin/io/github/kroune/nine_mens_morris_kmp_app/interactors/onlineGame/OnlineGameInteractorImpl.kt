package io.github.kroune.nine_mens_morris_kmp_app.interactors.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.onlineGameRepository
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameInfo
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import kotlinx.coroutines.channels.Channel

class OnlineGameInteractorImpl : OnlineGameInteractorI {
    val repository = onlineGameRepository

    override suspend fun connect(
        gameId: Long,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        return repository.connect(
            gameId,
            jwtTokenInteractor.getJwtToken()!!,
            channelToReceiveMoves
        )
    }

    override suspend fun giveUp(gameId: Long): GiveUpApiResponse {
        return repository.giveUp(
            gameId,
            jwtTokenInteractor.getJwtToken()!!
        )
    }

    override suspend fun sendMove(
        move: Movement,
        gameId: Long,
    ): SendMoveApiResponse {
        return repository.sendMove(
            move,
            gameId,
            jwtTokenInteractor.getJwtToken()!!
        )
    }
}