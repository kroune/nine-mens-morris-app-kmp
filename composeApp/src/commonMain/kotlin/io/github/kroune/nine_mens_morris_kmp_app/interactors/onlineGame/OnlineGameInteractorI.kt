package io.github.kroune.nine_mens_morris_kmp_app.interactors.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameEvent
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import kotlinx.coroutines.flow.Flow

interface OnlineGameInteractorI {
    suspend fun connect(
        gameId: Long,
    ): Flow<GameEvent>

    suspend fun giveUp(
        gameId: Long
    ): GiveUpApiResponse

    suspend fun sendMove(
        move: Movement,
        gameId: Long,
    ): SendMoveApiResponse
}