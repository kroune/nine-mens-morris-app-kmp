package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnlineGameRepositoryImpl : OnlineGameRepositoryI {
    override suspend fun connectToGame(gameId: Long, jwtToken: String): DefaultClientWebSocketSession {
        var session: DefaultClientWebSocketSession? = null
        val gettingSessionJob = Job()
        CoroutineScope(Dispatchers.Default).launch {
            network.webSocket("ws$SERVER_ADDRESS$USER_API/game",
                request = {
                    url {
                        parameters["jwtToken"] = jwtToken
                        parameters["gameId"] = gameId.toString()
                    }
                }) {
                session = this
                gettingSessionJob.complete()
            }
        }
        gettingSessionJob.join()
        return session!!
    }
}