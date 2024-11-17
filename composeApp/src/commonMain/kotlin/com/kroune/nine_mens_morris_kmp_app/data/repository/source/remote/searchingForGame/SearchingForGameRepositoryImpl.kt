package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class SearchingForGameRepositoryImpl : SearchingForGameRepositoryI {
    override suspend fun connect(
        jwtToken: String
    ): Pair<ReceiveChannel<Frame>, suspend () -> Unit> {
        var session: DefaultClientWebSocketSession? = null
        val gettingSessionJob = Job()
        CoroutineScope(Dispatchers.Default).launch {
            network.webSocket("ws$SERVER_ADDRESS$USER_API/search-for-game",
                request = {
                    url {
                        parameters["jwtToken"] = jwtToken
                    }
                }) {
                session = this
                gettingSessionJob.complete()
                closeReason.join()
            }
        }
        gettingSessionJob.join()
        return Pair(session!!.incoming, { session!!.close() })
    }
}