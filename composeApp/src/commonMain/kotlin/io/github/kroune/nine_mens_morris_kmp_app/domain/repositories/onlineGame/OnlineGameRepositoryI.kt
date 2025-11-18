package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import kotlinx.coroutines.flow.Flow

interface OnlineGameRepositoryI {
    fun connect(
        gameId: Long,
        channelToSendMoves: Flow<Movement>,
    ): Flow<GameEvent>
}
