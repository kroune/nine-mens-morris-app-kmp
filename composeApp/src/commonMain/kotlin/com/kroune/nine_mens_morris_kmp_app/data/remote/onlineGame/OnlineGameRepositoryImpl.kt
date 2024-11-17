package com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.common.receiveDeserialized
import com.kroune.nine_mens_morris_kmp_app.common.receiveText
import com.kroune.nine_mens_morris_kmp_app.common.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
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
        var onClose = suspend {}
        CoroutineScope(Dispatchers.Default).launch {
            network.webSocket("ws$SERVER_ADDRESS$USER_API/game",
                request = {
                    url {
                        parameters["jwtToken"] = jwtToken
                        parameters["gameId"] = gameId.toString()
                    }
                }) {
                onClose = {
                    println("closing connection")
                    this.close()
                }
                receivedIsGreenStatus.complete(this@webSocket.receiveText().toBooleanStrict())
                positionReceivedOnConnection.complete(this@webSocket.receiveDeserialized<Position>())
                enemyId.complete(this@webSocket.receiveText().toLong())
                CoroutineScope(Dispatchers.Default).launch {
                    while (true) {
                        val movement = channelToSendMoves.receive()
                        this@webSocket.sendSerialized(movement)
                    }
                    // this exception will be thrown when channel is closed
                }
                while (true) {
                    val move = this.receiveDeserialized<Movement>()
                    channelToReceiveMoves.send(move)
                }
            }
        }
        return Pair(
            GameInfo(receivedIsGreenStatus, positionReceivedOnConnection, enemyId),
            onClose
        )
    }
}