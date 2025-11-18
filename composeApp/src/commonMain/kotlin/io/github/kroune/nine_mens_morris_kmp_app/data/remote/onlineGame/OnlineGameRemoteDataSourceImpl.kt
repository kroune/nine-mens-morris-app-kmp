package io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.data.receiveDeserialized
import io.github.kroune.nine_mens_morris_kmp_app.data.receiveDeserializedCatching
import io.github.kroune.nine_mens_morris_kmp_app.data.sendSerializedCatching
import io.github.kroune.nine_mens_morris_kmp_app.data.wsApi
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.websocket.close
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OnlineGameRemoteDataSourceImpl : OnlineGameRemoteDataSourceI {
    /**
     * @return Triple of [SendChannel] [ReceiveChannel] Unit to close connection
     */
    override fun connect(
        gameId: Long,
        jwtToken: String,
        channelToSendMoves: Flow<Movement>,
    ): Flow<GameEvent> {
        return flow {
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
                // if we have green pieces
                val receivedIsGreenStatus = this@wss.receiveDeserialized<Boolean>()
                // enemy id
                val enemyId = this@wss.receiveDeserialized<Long>()
                // game start position
                val positionReceivedOnConnection = this@wss.receiveDeserialized<Position>()

                emit(
                    GameEvent.Success.GameInfo(
                        receivedIsGreenStatus,
                        positionReceivedOnConnection,
                        enemyId,
                    )
                )
                launch {
                    channelToSendMoves.collect { movement ->
                        val sendResult = this@wss.sendSerializedCatching(movement)
                        if (sendResult != null) {
                            // something went wrong
                            log("failed to send a move $movement", sendResult, Severity.ERROR)
                            this@wss.cancel()
                            close()
                        }
                        log("sent a move $movement", severity = Severity.DEBUG)
                        // this basically means we gave up
                        if (movement == Movement(null, null)) {
                            log("user gave up", severity = Severity.INFO)
                            this@wss.cancel()
                            close()
                        }
                    }
                }
                while (currentCoroutineContext().isActive) {
                    val move = receiveDeserializedCatching<Movement>().getOrElse {
                        log("failed to send a receive a move", it, Severity.ERROR)
                        this@wss.cancel()
                        close()
                        return@wss
                    }
                    println("received move $move")
                    if (move == Movement(null, null)) {
                        emit(GameEvent.Success.GameEnded)
                        this@wss.cancel()
                        close()
                        break
                    }
                    emit(GameEvent.Success.Move(move))
                }
            }
        }
    }
}
