package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.searchingForGame

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession

interface SearchingForGameRepositoryI {
    suspend fun connectToSearchingForGame(
        jwtToken: String
    ): DefaultClientWebSocketSession
}