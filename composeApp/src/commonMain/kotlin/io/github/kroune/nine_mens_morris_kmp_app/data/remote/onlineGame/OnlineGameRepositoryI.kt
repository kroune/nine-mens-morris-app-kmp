package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import kotlinx.coroutines.flow.Flow

interface OnlineGameRepositoryI {
    suspend fun connect(
        gameId: Long,
        jwtToken: String,
    ): Flow<GameEvent>

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
