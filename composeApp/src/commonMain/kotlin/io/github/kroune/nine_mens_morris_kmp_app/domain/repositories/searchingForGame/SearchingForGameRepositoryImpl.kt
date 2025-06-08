package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import kotlinx.coroutines.channels.Channel

class SearchingForGameRepositoryImpl(
    private var searchingForGameRemoteDataSource: SearchingForGameRemoteDataSourceI,
    private val jwtTokenRepository: JwtTokenRepositoryI
) : SearchingForGameRepositoryI {
    override suspend fun searchForGame(channel: Channel<Long>): SearchingForGameResponse {
        val jwtToken =
            jwtTokenRepository.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return searchingForGameRemoteDataSource.connect(channel, jwtToken)
    }
}