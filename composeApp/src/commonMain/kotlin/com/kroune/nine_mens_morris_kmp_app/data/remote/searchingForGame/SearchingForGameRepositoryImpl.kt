package com.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.common.receiveTextCatching
import com.kroune.nine_mens_morris_kmp_app.data.remote.SearchingForGameResponses
import com.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
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
        val session: CompletableDeferred<DefaultClientWebSocketSession?> = CompletableDeferred(null)
        val gameId = CompletableDeferred<Result<Long>?>(null)
        CoroutineScope(Dispatchers.Default).launch {
            var gameIdValue: Long? = null
            val result = runCatching {
                network.webSocket("ws$SERVER_ADDRESS$USER_API/search-for-game",
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
                            // TODO: this is debug, remove later
                            println(
                                "error happened, when searching for game closeReason = ${closeReason.getCompleted()?.message} exception - ${
                                    textResult.exceptionOrNull()!!.printStackTrace()
                                }"
                            )
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
            }.recoverNetworkError(SearchingForGameResponses.NetworkError)
            // TODO: handle other errors
            gameId.complete(result)
        }
        return Pair(gameId, { session.await()!!.close() })
    }
}