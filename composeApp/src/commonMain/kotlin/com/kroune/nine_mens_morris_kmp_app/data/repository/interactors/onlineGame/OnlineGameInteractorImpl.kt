package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

import androidx.compose.runtime.mutableStateOf
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.onlineGameRepository
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class OnlineGameInteractorImpl : OnlineGameInteractorI {
    val repository = onlineGameRepository

    override var receivedIsGreenStatus: Boolean? = null
    override var receivedMovesChannel: Channel<Movement> = Channel()
    override var movesToSendChannel: Channel<Movement> = Channel()
    override var gameEnded = mutableStateOf<Boolean>(false)
    override var positionReceivedOnConnection: Position? = null
    override var enemyId: Long? = null
    override var gettingBasicInfoJob = Job()

    private var session: DefaultClientWebSocketSession? = null

    override suspend fun connect(gameId: Long) {
        val jwtToken = jwtTokenInteractor.getJwtToken()
            ?: error("connect to game function was invoked, but jwt token is null")

        session?.close()
        session = null
        receivedIsGreenStatus = null
        receivedMovesChannel = Channel()
        movesToSendChannel = Channel()
        gameEnded.value = false
        positionReceivedOnConnection = null
        enemyId = null
        gettingBasicInfoJob = Job()

        session = repository.connectToGame(gameId, jwtToken)
        val incomingFlow = session!!.incoming

        receivedIsGreenStatus =
            (incomingFlow.receive() as Frame.Text).readText().toBooleanStrict()
        positionReceivedOnConnection = session!!.receiveDeserialized<Position>()
        enemyId = (incomingFlow.receive() as Frame.Text).readText().toLong()

        gettingBasicInfoJob.complete()

        CoroutineScope(Dispatchers.Default).launch {
            movesToSendChannel.consumeEach { movement ->
                session!!.sendSerialized(movement)
            }
        }
        while (true) {
            val move = session!!.receiveDeserialized<Movement>()
            if (move.startIndex == null && move.endIndex == null) {
                gameEnded.value = true
                session!!.close(CloseReason(200, "game ended"))
                return
            }
            receivedMovesChannel.send(move)
        }
    }

    override suspend fun giveUp() {
        session?.sendSerialized<Movement>(Movement(null, null))
    }
}