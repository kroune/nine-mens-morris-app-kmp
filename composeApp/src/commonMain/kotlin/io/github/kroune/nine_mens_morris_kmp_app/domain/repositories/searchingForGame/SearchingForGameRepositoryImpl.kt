package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.flow.MutableSharedFlow

class SearchingForGameRepositoryImpl(
    private var searchingForGameRemoteDataSource: SearchingForGameRemoteDataSourceI,
    private val jwtTokenRepository: JwtTokenRepositoryI,
) : SearchingForGameRepositoryI {
    override suspend fun searchForGame(channel: MutableSharedFlow<Long>): SearchingForGameResponse {
        val jwtToken =
            jwtTokenRepository.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return searchingForGameRemoteDataSource.connect(channel, jwtToken)
    }
}
