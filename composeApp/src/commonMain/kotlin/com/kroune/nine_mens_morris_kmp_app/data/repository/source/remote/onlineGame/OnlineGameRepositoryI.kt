package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession

sealed interface OnlineGameRepositoryI {
    suspend fun connectToGame(gameId: Long, jwtToken: String): DefaultClientWebSocketSession
}