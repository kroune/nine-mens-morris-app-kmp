package com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGame

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

interface SearchingForGameInteractorI {
    suspend fun searchForGame(channelToSendExpectedTime: Channel<Long>): Pair<CompletableDeferred<Result<Long>?>, suspend () -> Unit>
}