package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import kotlinx.coroutines.flow.MutableSharedFlow

interface SearchingForGameRemoteDataSourceI {
    suspend fun connect(
        channel: MutableSharedFlow<Long>,
        jwtToken: String,
    ): SearchingForGameResponse
}