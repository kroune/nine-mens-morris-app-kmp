package io.github.kroune.nine_mens_morris_kmp_app.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.model.api.SearchingForGameResponse
import kotlinx.coroutines.channels.Channel

interface SearchingForGameRepositoryI {
    suspend fun searchForGame(channel: Channel<Long>): SearchingForGameResponse
}