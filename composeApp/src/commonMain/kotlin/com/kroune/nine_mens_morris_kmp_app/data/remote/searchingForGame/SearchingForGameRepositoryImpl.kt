package com.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.common.receiveTextCatching
import com.kroune.nine_mens_morris_kmp_app.common.serverApi
import com.kroune.nine_mens_morris_kmp_app.data.remote.SearchingForGameResponses
import com.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SearchingForGameRepositoryImpl : SearchingForGameRepositoryI {
    override suspend fun connect(
        channelToSendExpectedTime: Channel<Long>,
        jwtToken: String
    ): Pair<CompletableDeferred<Result<Long>?>, suspend () -> Unit> {
        val route = serverApi {
            protocol = URLProtocol.WSS
            appendPathSegments("search-for-game")
        }.toString()
        val session: CompletableDeferred<DefaultClientWebSocketSession?> = CompletableDeferred(null)
        val gameId = CompletableDeferred<Result<Long>?>(null)
        CoroutineScope(Dispatchers.Default).launch {
            var gameIdValue: Long? = null
            val result = runCatching {
                network.wss(route,
                    request = {
                        url {
                            parameters["jwtToken"] = jwtToken
                        }
                    }
                ) {
                    session.complete(this)
                    while (true) {
                        val textResult = receiveTextCatching()
                        if (textResult.isFailure) {
                            // some error
                            break
                        }
                        val text = textResult.getOrThrow()
                        println("received $text")
                        val serverData = Json.decodeFromString<Pair<Boolean, Long>>(text)
                        if (serverData.first) {
                            channelToSendExpectedTime.send(serverData.second)
                        } else {
                            gameIdValue = serverData.second
                            channelToSendExpectedTime.close()
                            close()
                            break
                        }
                    }
                }
                gameIdValue!!
            }.recoverNetworkError(SearchingForGameResponses.NetworkError).onFailure {
                println("error when searching for game \n url - $route \n stacktrace - ${it.stackTraceToString()}")
            }
            // TODO: handle other errors
            gameId.complete(result)
        }
        return Pair(gameId, {
            session.await()!!.close()
        })
    }
}