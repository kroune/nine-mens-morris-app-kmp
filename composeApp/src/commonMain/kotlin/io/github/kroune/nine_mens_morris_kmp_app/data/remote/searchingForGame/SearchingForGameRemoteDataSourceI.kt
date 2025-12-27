package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import kotlinx.coroutines.flow.Flow

interface SearchingForGameRemoteDataSourceI {
    fun connect(
        jwtToken: String,
    ): Flow<SearchingForGameEvent>
}
