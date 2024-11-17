package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.searchingForGame

import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.ReceiveChannel

interface SearchingForGameRepositoryI {
    suspend fun connect(
        jwtToken: String
    ): Pair<ReceiveChannel<Frame>, suspend () -> Unit>
}