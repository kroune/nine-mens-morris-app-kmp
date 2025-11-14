package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.flow.Flow

class SearchingForGameRepositoryImpl(
    private var searchingForGameRemoteDataSource: SearchingForGameRemoteDataSourceI,
    private val jwtTokenRepository: JwtTokenRepositoryI,
) : SearchingForGameRepositoryI {
    override fun searchForGame(): Flow<SearchingForGameEvent> {
        val jwtToken = requireNotNull(jwtTokenRepository.getJwtToken()) {
            "jwt token is null when searching for game"
        }
        return searchingForGameRemoteDataSource.connect(jwtToken)
    }
}
