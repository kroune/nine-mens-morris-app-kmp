package io.github.kroune.nine_mens_morris_kmp_app.data.remote.game

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.other.PastGamesHistoryItem
import io.github.kroune.nine_mens_morris_kmp_app.data.decodeServerEvent
import io.github.kroune.nine_mens_morris_kmp_app.data.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.data.receiveDeserialized
import io.github.kroune.nine_mens_morris_kmp_app.data.receiveDeserializedCatching
import io.github.kroune.nine_mens_morris_kmp_app.data.sendSerializedCatching
import io.github.kroune.nine_mens_morris_kmp_app.data.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.onNetworkError
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class GameRemoteDataSourceImpl : GameRemoteDataSourceI {
    /**
     * @return Triple of [SendChannel] [ReceiveChannel] Unit to close connection
     */
    override suspend fun connectToGame(
        gameId: Long,
        jwtToken: String,
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        val receivedIsGreenStatus = CompletableDeferred<Boolean>()
        val positionReceivedOnConnection = CompletableDeferred<Position>()
        val enemyId = CompletableDeferred<Long>()
        val session: CompletableDeferred<DefaultClientWebSocketSession?> = CompletableDeferred(null)
        val gameEnded: CompletableDeferred<Boolean> = CompletableDeferred()
        CoroutineScope(Dispatchers.Default).launch {
            val route = wsApi {
                appendPathSegments("game", "game")
            }.toString()
            network.wss(
                route,
                request = {
                    parameter("jwtToken", jwtToken)
                    parameter("gameId", gameId)
                }
            ) {
                var channelClosedNormally = false
                // session was created
                session.complete(this)
                // if we have green pieces
                receivedIsGreenStatus.complete(this@wss.receiveDeserialized<Boolean>())
                // enemy id
                enemyId.complete(this@wss.receiveDeserialized<Long>())
                // game start position
                positionReceivedOnConnection.complete(this@wss.receiveDeserialized<Position>())
                CoroutineScope(Dispatchers.Default).launch {
                    while (!gameEnded.isCompleted) {
                        val movementResult = channelToSendMoves.receiveCatching()
                        if (movementResult.isFailure) {
                            // channel was closed, we have exited from the game
                            break
                        }
                        val movement = movementResult.getOrThrow()
                        log("sent a move $movement", severity = Severity.DEBUG)
                        val sendResult = this@wss.sendSerializedCatching(movement)
                        if (sendResult != null && !channelClosedNormally) {
                            // something went wrong
                            log("failed to send a move $movement", sendResult, Severity.ERROR)
                            throw sendResult
                        }
                        // this basically means we gave up
                        if (movement == Movement(null, null)) {
                            log("user gave up", severity = Severity.INFO)
                            gameEnded.complete(true)
                            channelToSendMoves.close()
                            channelToReceiveMoves.close()
                            channelClosedNormally = true
                            close()
                            break
                        }
                    }
                }
                while (!gameEnded.isCompleted) {
                    val moveResult = this.receiveDeserializedCatching<Movement>()
                    // some error happened, cleaning up everything
                    moveResult.onFailure {
                        log("failed to send a receive a move", it, Severity.ERROR)
                        channelToSendMoves.close()
                        channelToReceiveMoves.close()
                        close()
                        if (!channelClosedNormally) {
                            log("channel was closed abnormally", it, Severity.ERROR)
                            throw it
                        }
                        return@wss
                    }
                    val move = moveResult.getOrThrow()
                    println("received move $move")
                    if (move == Movement(null, null)) {
                        println("game ended")
                        gameEnded.complete(true)
                        channelToSendMoves.close()
                        channelToReceiveMoves.close()
                        channelClosedNormally = true
                        close()
                        break
                    }
                    channelToReceiveMoves.trySend(move).onFailure {
                        // channel to receive moves was closed
                        channelToSendMoves.close()
                        channelToReceiveMoves.close()
                        close()
                    }
                }
            }
        }
        return Pair(
            GameInfo(receivedIsGreenStatus, positionReceivedOnConnection, enemyId, gameEnded),
            { session.await()!!.close() }
        )
    }

    override suspend fun searchForGame(
        channel: Channel<Long>,
        jwtToken: String
    ): SearchingForGameResponse {
        val route = wsApi {
            appendPathSegments("game", "search-for-game")
        }.toString()
        var result: SearchingForGameResponse = SearchingForGameResponse.UnknownError()
        runCatching {
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
                            val message = "error when using websocket ${
                                closeReason.await()
                                    .let { "reason - ${it?.knownReason}, code - ${it?.code}" }
                            }"
                            log(
                                message, it, Severity.ERROR
                            )
                            result = SearchingForGameResponse.UnknownError()
                            break
                        }
                }
            }
        }.onNetworkError {
            return SearchingForGameResponse.NetworkError()
        }
        return result
    }

    override suspend fun getPastGame(
        jwtToken: String,
        userId: Long,
        limit: Int,
        offset: Long,
    ): PastGamesApiResponse<List<PastGamesHistoryItem>> {
        val route = httpApi {
            appendPathSegments("game", "past-games")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("jwtToken", jwtToken)
                parameter("id", userId)
                parameter("limit", limit)
                parameter("offset", offset)
                accept(ContentType.Application.ProtoBuf)
            }
            pastGamesResult(request)
        }
            .recoverNetworkError(PastGamesApiResponse.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                PastGamesApiResponse.UnknownError()
            }
    }

    suspend fun pastGamesResult(request: HttpResponse): PastGamesApiResponse<List<PastGamesHistoryItem>> {
        return when (request.status) {
            HttpStatusCode.BadRequest -> {
                PastGamesApiResponse.UnknownError()
            }

            HttpStatusCode.Forbidden -> {
                PastGamesApiResponse.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                PastGamesApiResponse.ServerError()
            }

            else -> {
                PastGamesApiResponse.Success(
                    request.body<List<PastGamesHistoryItem>>()
                )
            }
        }
    }
}