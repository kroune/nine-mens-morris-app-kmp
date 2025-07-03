package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game

import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PastGamesHistoryItem
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.game.GameInfo
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.game.GameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.channels.Channel

class GameRepositoryImpl(
    private val jwtTokenRepository: JwtTokenRepositoryI,
    private val gameRemoteDataSource: GameRemoteDataSourceI
) : GameRepositoryI {
    override suspend fun connect(
        gameId: Long,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        return gameRemoteDataSource.connectToGame(
            gameId,
            jwtTokenRepository.getJwtToken()!!,
            channelToSendMoves,
            channelToReceiveMoves
        )
    }

    override suspend fun searchForGame(
        channel: Channel<Long>
    ): SearchingForGameResponse {
        val jwtToken =
            jwtTokenRepository.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return gameRemoteDataSource.searchForGame(channel, jwtToken)
    }

    override suspend fun getPastGames(
        accountId: Long,
        limit: Int,
        offset: Long
    ): PastGamesApiResponse<List<PastGamesHistoryItem>> {
        val jwtToken =
            jwtTokenRepository.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return gameRemoteDataSource.getPastGame(
            jwtToken = jwtToken,
            userId = accountId,
            limit = limit,
            offset = offset
        )
    }
}