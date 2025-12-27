package io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import io.github.kroune.nine_mens_morris_kmp_app.data.decodeServerEvent
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.data.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.onNetworkError
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.websocket.Frame
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class SearchingForGameRemoteDataSourceImpl : SearchingForGameRemoteDataSourceI {
    override fun connect(
        jwtToken: String,
    ): Flow<SearchingForGameEvent> {
        return flow {
            val route = wsApi {
                appendPathSegments("game", "search-for-game")
            }.toString()
            runCatching {
                network.wss(
                    route,
                    {
                        parameter("jwtToken", jwtToken)
                    }
                ) {
                    while (currentCoroutineContext().isActive) {
                        incoming.receiveCatching()
                            .onSuccess {
                                if (it !is Frame.Binary) {
                                    log("frame is not a binary", severity = Severity.INFO)
                                    return@onSuccess
                                }
                                val (data, metadata) = it.decodeServerEvent<Long, String>()
                                when (metadata) {
                                    "waiting_time" -> {
                                        emit(
                                            SearchingForGameEvent.Success.NewExpectedWaitingTime(
                                                data
                                            )
                                        )
                                    }

                                    "game_id" -> {
                                        emit(
                                            SearchingForGameEvent.Success.GameFound(
                                                data
                                            )
                                        )
                                        break
                                    }

                                    else -> {
                                        emit(SearchingForGameEvent.Error.NetworkError)
                                        log(
                                            "unknown metadata - $metadata, data - $data",
                                            severity = Severity.INFO
                                        )
                                    }
                                }
                            }
                            .onFailure {
                                val closeReasonString = closeReason.await().let { closeReason ->
                                    "reason - ${closeReason?.knownReason}, code - ${closeReason?.code}"
                                }
                                val message = "error when using websocket $closeReasonString"
                                log(message, it, Severity.ERROR)
                                emit(SearchingForGameEvent.Error.UnknownError)
                                break
                            }
                    }
                }
            }.onNetworkError {
                emit(SearchingForGameEvent.Error.NetworkError)
            }
            currentCoroutineContext().cancel()
        }
    }
}
