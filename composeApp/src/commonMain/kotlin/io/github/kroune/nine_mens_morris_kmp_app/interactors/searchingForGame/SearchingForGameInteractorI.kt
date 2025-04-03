package io.github.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.model.SearchingForGameResponse
import kotlinx.coroutines.channels.Channel

interface SearchingForGameInteractorI {
    suspend fun searchForGame(channel: Channel<Long>): SearchingForGameResponse
}