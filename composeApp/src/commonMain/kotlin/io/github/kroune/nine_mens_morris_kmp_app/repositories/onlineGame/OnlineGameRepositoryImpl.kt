package io.github.kroune.nine_mens_morris_kmp_app.repositories.onlineGame

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameInfo
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.channels.Channel

class OnlineGameRepositoryImpl(
    private val jwtTokenRepository: JwtTokenRepositoryI,
    private val onlineGameRemoteDataSource: OnlineGameRemoteDataSourceI
) : OnlineGameRepositoryI {
    override suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        return onlineGameRemoteDataSource.connect(
            gameId,
            jwtTokenRepository.getJwtToken()!!,
            channelToSendMoves,
            channelToReceiveMoves
        )
    }
}