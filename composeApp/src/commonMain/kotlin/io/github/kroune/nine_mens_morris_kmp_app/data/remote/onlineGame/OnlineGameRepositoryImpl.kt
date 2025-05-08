package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.common.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.common.receiveDeserialized
import io.github.kroune.nine_mens_morris_kmp_app.common.receiveDeserializedCatching
import io.github.kroune.nine_mens_morris_kmp_app.common.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.launch

class OnlineGameRepositoryImpl : OnlineGameRepositoryI {
    /**
     * @return Triple of [SendChannel] [ReceiveChannel] Unit to close connection
     */
    override suspend fun connect(
        gameId: Long,
        jwtToken: String,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        val receivedIsGreenStatus = CompletableDeferred<Boolean>()
        val positionReceivedOnConnection = CompletableDeferred<Position>()
        val enemyId = CompletableDeferred<Long>()
        val session: CompletableDeferred<DefaultClientWebSocketSession?> = CompletableDeferred(null)
        val gameEnded: CompletableDeferred<Boolean> = CompletableDeferred()
        CoroutineScope(Dispatchers.Default).launch {
            val route = wsApi {
                appendPathSegments("game", "gameWS")
                parameters["jwtToken"] = jwtToken
                parameters["gameId"] = gameId.toString()
            }.toString()
            network.wss(
                route
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
                while (!gameEnded.isCompleted) {
                    val moveResult = this.receiveDeserializedCatching<Movement>()
                    // some error happened, cleaning up everything
                    moveResult.onFailure {
                        log("failed to send a receive a move", it, Severity.ERROR)
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
                        channelToReceiveMoves.close()
                        channelClosedNormally = true
                        close()
                        break
                    }
                    channelToReceiveMoves.trySend(move).onFailure {
                        // channel to receive moves was closed
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

    override suspend fun giveUp(gameId: Long, jwtToken: String): GiveUpApiResponse {
        val route = httpApi {
            appendPathSegments("game", "give-up")
            parameters["jwtToken"] = jwtToken
            parameters["gameId"] = gameId.toString()
        }.toString()
        val request = network.post(route)
        return registerResult(request)
    }

    private fun registerResult(request: HttpResponse): GiveUpApiResponse {
        return when (request.status) {
            HttpStatusCode.InternalServerError -> {
                GiveUpApiResponse.ServerError
            }

            HttpStatusCode.OK -> {
                GiveUpApiResponse.Success
            }

            else -> {
                GiveUpApiResponse.UnknownError
            }
        }
    }

    override suspend fun sendMove(
        move: Movement,
        gameId: Long,
        jwtToken: String
    ): SendMoveApiResponse {
        val route = httpApi {
            appendPathSegments("game", "move")
            parameters["jwtToken"] = jwtToken
            parameters["gameId"] = gameId.toString()
        }.toString()
        val request = network.post(route) {
            contentType(ContentType.Application.Json)
            setBody(move)
        }
        return sendMoveResult(request)
    }

    private fun sendMoveResult(request: HttpResponse): SendMoveApiResponse {
        return when (request.status) {
            HttpStatusCode.InternalServerError -> {
                SendMoveApiResponse.ServerError
            }

            HttpStatusCode.OK -> {
                SendMoveApiResponse.Success
            }

            else -> {
                SendMoveApiResponse.UnknownError
            }
        }
    }
}