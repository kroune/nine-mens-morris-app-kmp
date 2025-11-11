package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import kotlinx.coroutines.flow.MutableSharedFlow

interface SearchingForGameRepositoryI {
    suspend fun searchForGame(channel: MutableSharedFlow<Long>): SearchingForGameResponse
}