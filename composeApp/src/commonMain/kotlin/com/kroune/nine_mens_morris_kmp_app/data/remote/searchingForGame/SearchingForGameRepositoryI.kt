package com.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

interface SearchingForGameRepositoryI {
    suspend fun connect(
        channelToSendExpectedTime: Channel<Long>,
        jwtToken: String
    ): Pair<CompletableDeferred<Result<Long>?>, suspend () -> Unit>
}