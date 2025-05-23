package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.common.decodeServerEvent
import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.common.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.model.SearchingForGameResponse
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.io.IOException

class SearchingForGameRepositoryImpl : SearchingForGameRepositoryI {
    override suspend fun connect(
        channel: Channel<Long>,
        jwtToken: String
    ): SearchingForGameResponse {
        val route = wsApi {
            appendPathSegments("search-for-game")
        }.toString()
        var result: SearchingForGameResponse = SearchingForGameResponse.UnknownError()
        network.wss(
            route,
            {
                parameter("jwtToken", jwtToken)
            }
        ) {
            while (true) {
                incoming.receiveCatching()
                    .onSuccess {
                        if (it !is Frame.Binary) {
                            println("not a binary")
                            return@onSuccess
                        }
                        val (data, metadata) = it.decodeServerEvent<Long, String>()
                        when (metadata) {
                            "waiting_time" -> {
                                val waitingTime = data
                                channel.send(waitingTime)
                            }

                            "game_id" -> {
                                result = SearchingForGameResponse.Success(data)
                                break
                            }

                            else -> {
                                log(
                                    "unknown metadata - $metadata, data - $data",
                                    severity = Severity.INFO
                                )
                                println("FUCK")
                                metadata
                            }
                        }
                    }
                    .onClosed {
                        log("websocket closed", it, Severity.INFO)
                        result = if (it is IOException) {
                            SearchingForGameResponse.NetworkError()
                        } else {
                            SearchingForGameResponse.UnknownError()
                        }
                        break
                    }
                    .onFailure {
                        log(
                            "error when using websocket ${
                                closeReason.await()
                                    .let { "reason - ${it?.knownReason}, code - ${it?.code}" }
                            }", it, Severity.ERROR
                        )
                        result = SearchingForGameResponse.UnknownError()
                        break
                    }
            }
        }
        return result
    }
}