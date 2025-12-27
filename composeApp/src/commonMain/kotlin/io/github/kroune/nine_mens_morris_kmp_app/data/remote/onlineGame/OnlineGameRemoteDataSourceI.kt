package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import kotlinx.coroutines.flow.Flow

interface OnlineGameRemoteDataSourceI {
    fun connect(
        gameId: Long,
        jwtToken: String,
        channelToSendMoves: Flow<Movement>,
    ): Flow<GameEvent>
}
