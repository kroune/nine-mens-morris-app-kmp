package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.model.api.SearchingForGameResponse
import kotlinx.coroutines.channels.Channel

interface SearchingForGameRepositoryI {
    suspend fun connect(channel: Channel<Long>, jwtToken: String): SearchingForGameResponse
}