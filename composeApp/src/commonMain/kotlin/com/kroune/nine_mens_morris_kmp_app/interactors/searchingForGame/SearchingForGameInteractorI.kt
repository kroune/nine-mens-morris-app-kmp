package com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import kotlinx.coroutines.channels.Channel

interface SearchingForGameInteractorI {
    var gameId: Long?
    suspend fun disconnect()
    suspend fun searchForGame(channelToSendExpectedTime: Channel<Long>)
}