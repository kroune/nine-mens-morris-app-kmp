package io.github.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.model.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.data.searchingForGameRepository
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import kotlinx.coroutines.channels.Channel

class SearchingForGameInteractorImpl : SearchingForGameInteractorI {
    override suspend fun searchForGame(channel: Channel<Long>): SearchingForGameResponse {
        val jwtToken =
            jwtTokenInteractor.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return searchingForGameRepository.connect(channel, jwtToken)
    }
}