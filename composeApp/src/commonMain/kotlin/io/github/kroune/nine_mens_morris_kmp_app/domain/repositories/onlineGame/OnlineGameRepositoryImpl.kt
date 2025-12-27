package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.flow.Flow

class OnlineGameRepositoryImpl(
    private val jwtTokenRepository: JwtTokenRepositoryI,
    private val onlineGameRemoteDataSource: OnlineGameRemoteDataSourceI
) : OnlineGameRepositoryI {
    override fun connect(
        gameId: Long,
        channelToSendMoves: Flow<Movement>,
    ): Flow<GameEvent> {
        return onlineGameRemoteDataSource.connect(
            gameId,
            jwtTokenRepository.getJwtToken()!!,
            channelToSendMoves
        )
    }
}
