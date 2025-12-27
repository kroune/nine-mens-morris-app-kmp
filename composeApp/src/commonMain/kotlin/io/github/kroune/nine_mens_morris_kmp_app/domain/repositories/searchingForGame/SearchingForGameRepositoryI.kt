package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import kotlinx.coroutines.flow.Flow

interface SearchingForGameRepositoryI {
    fun searchForGame(): Flow<SearchingForGameEvent>
}
