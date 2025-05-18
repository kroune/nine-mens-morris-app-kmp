package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.common.ServerEvent
import io.github.kroune.nine_mens_morris_kmp_app.common.decodeProtobuf
import io.github.kroune.nine_mens_morris_kmp_app.common.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.common.receiveDeserialized
import io.github.kroune.nine_mens_morris_kmp_app.common.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.model.GiveUpApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.SendMoveApiResponse
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class OnlineGameRepositoryImpl : OnlineGameRepositoryI {
    /**
     * @return Triple of [SendChannel] [ReceiveChannel] Unit to close connection
     */
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun connect(
        gameId: Long,
        jwtToken: String
    ): Flow<GameEvent> {
        return flow {
            val route = wsApi {
                appendPathSegments("game", "gameWS")
                parameters["jwtToken"] = jwtToken
                parameters["gameId"] = gameId.toString()
            }
            network.wss(
                route.toString()
            ) {
                // if we have green pieces
                val isGreen = receiveDeserialized<Boolean>()
                emit(GameEvent.IsGreenEvent(isGreen))
                // enemy id
                val enemyId = receiveDeserialized<Long>()
                emit(GameEvent.EnemyIdEvent(enemyId))
                // game start position
                val position = receiveDeserialized<Position>()
                emit(GameEvent.PositionEvent(position))

                while (isActive) {
                    val it = incoming.receive()
                    if (it !is Frame.Binary)
                        return@wss
                    val (metadata, data) = ProtoBuf.decodeFromByteArray<ServerEvent>(it.data)
                    println("metadata - $metadata")
                    when (metadata) {
                        "move" -> {
                            emit(GameEvent.MovementEvent(data.decodeProtobuf()))
                        }

                        "game-end" -> {
                            emit(GameEvent.GameEnd(data.decodeProtobuf()))
                            close()
                        }
                    }
                }
            }
        }.buffer(20000, BufferOverflow.DROP_OLDEST)
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

sealed interface GameEvent {
    data class IsGreenEvent(val isGreen: Boolean) : GameEvent
    data class EnemyIdEvent(val enemyId: Long) : GameEvent
    data class PositionEvent(val position: Position) : GameEvent
    data class MovementEvent(val data: Movement) : GameEvent
    data class GameEnd(val reason: String) : GameEvent
}