package com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.common.receiveDeserialized
import com.kroune.nine_mens_morris_kmp_app.common.receiveDeserializedCatching
import com.kroune.nine_mens_morris_kmp_app.common.sendSerializedCatching
import com.kroune.nine_mens_morris_kmp_app.common.serverApi
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
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
        channelToSendMoves: Channel<Movement>,
        channelToReceiveMoves: Channel<Movement>
    ): Pair<GameInfo, suspend () -> Unit> {
        val receivedIsGreenStatus = CompletableDeferred<Boolean>()
        val positionReceivedOnConnection = CompletableDeferred<Position>()
        val enemyId = CompletableDeferred<Long>()
        val session: CompletableDeferred<DefaultClientWebSocketSession?> = CompletableDeferred(null)
        val gameEnded: CompletableDeferred<Boolean> = CompletableDeferred()
        CoroutineScope(Dispatchers.Default).launch {
            val route = serverApi {
                protocol = URLProtocol.WSS
                appendPathSegments("game")
            }.toString()
            network.wss(route,
                request = {
                    url {
                        parameters["jwtToken"] = jwtToken
                        parameters["gameId"] = gameId.toString()
                    }
                }) {
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
                        println("sent move $movement")
                        val sendResult = this@wss.sendSerializedCatching(movement)
                        if (sendResult != null && !channelClosedNormally) {
                            // something went wrong
                            println("received exception when sending move ${sendResult.stackTraceToString()}")
                            throw sendResult
                        }
                        // this basically means we gave up
                        if (movement == Movement(null, null)) {
                            println("we gave up")
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
                    if (moveResult.exceptionOrNull() != null) {
                        println(
                            "move result exception ${
                                moveResult.exceptionOrNull()!!.printStackTrace()
                            }"
                        )
                        channelToSendMoves.close()
                        channelToReceiveMoves.close()
                        close()
                        if (!channelClosedNormally) {
                            println("channel was closed ${moveResult.exceptionOrNull()}")
                            moveResult.getOrThrow()
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
}