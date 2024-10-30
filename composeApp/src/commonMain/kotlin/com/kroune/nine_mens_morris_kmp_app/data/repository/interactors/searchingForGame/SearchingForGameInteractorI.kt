package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.searchingForGame

import kotlinx.coroutines.channels.Channel

interface SearchingForGameInteractorI {
    val channel: Channel<Long>
    var gameId: Long?
    suspend fun disconnect()
    suspend fun searchForGame()
}