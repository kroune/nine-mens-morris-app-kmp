package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

import androidx.compose.runtime.mutableStateOf
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.onlineGameRepository
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.serialization.WebsocketDeserializeException
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.utils.io.InternalAPI
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.serialization.receiveDeserializedBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class OnlineGameInteractorImpl : OnlineGameInteractorI {
    val repository = onlineGameRepository

    override var receivedIsGreenStatus: Boolean? = null
    override var receivedMovesChannel: Channel<Movement> = Channel()
    override var gameEnded = mutableStateOf<Boolean>(false)
    override var positionReceivedOnConnection: Position? = null
    override var enemyId: Long? = null

    private var session: DefaultClientWebSocketSession? = null

    @OptIn(InternalAPI::class)
    override suspend fun connect(gameId: Long, channelToSendMoves: Channel<Movement>) {
        val jwtToken = jwtTokenInteractor.getJwtToken()
            ?: error("connect to game function was invoked, but jwt token is null")

        session?.close()
        session = null
        receivedIsGreenStatus = null
        receivedMovesChannel = Channel()
        gameEnded.value = false
        positionReceivedOnConnection = null
        enemyId = null

        session = repository.connectToGame(gameId, jwtToken)
        val incomingFlow = session!!.incoming

        receivedIsGreenStatus =
            (incomingFlow.receive() as Frame.Text).readText().toBooleanStrict()
        positionReceivedOnConnection = session!!.receiveDeserialized<Position>()
        enemyId = (incomingFlow.receive() as Frame.Text).readText().toLong()

        CoroutineScope(Dispatchers.Default).launch {
            channelToSendMoves.consumeEach { movement ->
                session!!.sendSerialized(movement)
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val move = session!!.receiveDeserialized<Movement>()
                if (move.startIndex == null && move.endIndex == null) {
                    gameEnded.value = true
                    session!!.close(CloseReason(200, "game ended"))
                    return@launch
                }
                receivedMovesChannel.send(move)
            }
        }
    }

    override suspend fun giveUp() {
        session?.sendSerialized<Movement>(Movement(null, null))
    }
}