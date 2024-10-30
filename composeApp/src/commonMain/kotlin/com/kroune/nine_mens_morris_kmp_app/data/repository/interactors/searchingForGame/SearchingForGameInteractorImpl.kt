package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.searchingForGameRepository
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

class SearchingForGameInteractorImpl : SearchingForGameInteractorI {
    override var channel = Channel<Long>()
    override var gameId: Long? = null
    private var session: DefaultClientWebSocketSession? = null

    override suspend fun disconnect() {
        session?.close()
    }

    override suspend fun searchForGame() {
        // reset values
        channel = Channel<Long>()
        gameId = null
        val jwtToken =
            jwtTokenInteractor.getJwtToken() ?: error("jwt token is null when searching for game")
        session = searchingForGameRepository.connectToSearchingForGame(jwtToken)
        val incomingFlow = session!!.incoming
        incomingFlow.consumeEach {
            val text = (it as Frame.Text).readText()
            val serverData = Json.decodeFromString<Pair<Boolean, Long>>(text)
            if (!serverData.first) {
                gameId = serverData.second
                channel.close()
                session!!.close()
                return@consumeEach
            }
            channel.send(serverData.second)
        }
    }
}