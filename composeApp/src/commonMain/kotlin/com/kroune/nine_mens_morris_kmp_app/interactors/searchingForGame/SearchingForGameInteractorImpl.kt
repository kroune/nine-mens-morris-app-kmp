package com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import com.kroune.nine_mens_morris_kmp_app.common.receiveText
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.searchingForGameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SearchingForGameInteractorImpl : SearchingForGameInteractorI {
    override var gameId: Long? = null
    private var closeFunction: (suspend () -> Unit)? = null

    override suspend fun disconnect() {
        closeFunction!!()
    }

    override suspend fun searchForGame(channelToSendExpectedTime: Channel<Long>) {
        // reset values
        gameId = null
        CoroutineScope(Dispatchers.Default).launch {
            val jwtToken =
                jwtTokenInteractor.getJwtToken()
                    ?: error("jwt token is null when searching for game")
            val (receiveChannel, close) = searchingForGameRepository.connect(jwtToken)
            closeFunction = close
            try {
                while (true) {
                    val text = receiveChannel.receiveText()
                    val serverData = Json.decodeFromString<Pair<Boolean, Long>>(text)
                    if (serverData.first) {
                        channelToSendExpectedTime.send(serverData.second)
                    } else {
                        gameId = serverData.second
                        disconnect()
                        channelToSendExpectedTime.close()
                        break
                    }
                }
            } catch (e: Exception) {
                println("SOME ERROR")
            }
        }.join()
    }
}