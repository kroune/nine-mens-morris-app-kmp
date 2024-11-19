package com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.data.searchingForGameRepository
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

class SearchingForGameInteractorImpl : SearchingForGameInteractorI {
    override suspend fun searchForGame(channelToSendExpectedTime: Channel<Long>):
            Pair<CompletableDeferred<Result<Long>?>, suspend () -> Unit> {
        val jwtToken =
            jwtTokenInteractor.getJwtToken()
                ?: error("jwt token is null when searching for game")
        return searchingForGameRepository.connect(channelToSendExpectedTime, jwtToken)
    }
}