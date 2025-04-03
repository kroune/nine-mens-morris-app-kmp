package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.common.decodeServerEvent
import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.common.serverApi
import io.github.kroune.nine_mens_morris_kmp_app.model.SearchingForGameResponse
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onSuccess
import kotlinx.io.IOException

class SearchingForGameRepositoryImpl : SearchingForGameRepositoryI {
    override suspend fun connect(
        channel: Channel<Long>,
        jwtToken: String
    ): SearchingForGameResponse {
        val route = serverApi {
            protocol = URLProtocol.WSS
            appendPathSegments("search-for-game")
        }.toString()
        var result: SearchingForGameResponse = SearchingForGameResponse.UnknownError()
        network.wss(route, {
            url {
                parameter("jwtToken", jwtToken)
            }
        }) {
            var shouldBreak = false
            while (!shouldBreak) {
                incoming.tryReceive()
                    .onSuccess {
                        if (it !is Frame.Binary)
                            return@wss
                        val (data, metadata) = it.decodeServerEvent<Long, String>()
                        when (metadata) {
                            "waiting_time" -> {
                                val waitingTime = data
                                channel.send(waitingTime)
                            }

                            "game_id" -> {
                                result = SearchingForGameResponse.Success(data)
                                shouldBreak = true
                            }

                            else -> {
                                println("FUCK")
                                metadata
                            }
                        }
                    }
                    .onClosed {
                        result = if (it is IOException) {
                            SearchingForGameResponse.NetworkError()
                        } else {
                            SearchingForGameResponse.UnknownError()
                        }
                        shouldBreak = true
                    }
            }
        }
        return result
    }
}