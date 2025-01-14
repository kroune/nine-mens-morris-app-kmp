package com.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.event.game.SearchingForGameScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGameInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class SearchingForGameComponent(
    onGameFind: (Long) -> Unit,
    val onGoingToWelcomeScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    var expectedWaitingTime = Channel<Long>(10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val disconnect: CompletableDeferred<suspend () -> Unit> = CompletableDeferred()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val (gameIdDeferred, onClose) = searchingForGameInteractor.searchForGame(
                expectedWaitingTime
            )
            disconnect.complete {
                onClose()
            }
            val gameId = gameIdDeferred.await()
            if (gameId == null) {
                println("returned game id was null")
                withContext(Dispatchers.Main) {
                    onEvent(SearchingForGameScreenEvent.Back)
                }
                return@launch
            }
            gameId.fold(
                onSuccess = {
                    println("found game, id = $it")
                    withContext(Dispatchers.Main) {
                        onGameFind(it)
                    }
                },
                onFailure = {
                    if (it is CancellationException)
                    // that's ok
                        return@launch
                    // TODO: log error
                    println("getting game id failed")
                    it.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onEvent(SearchingForGameScreenEvent.Back)
                    }
                    return@launch
                }
            )
        }
    }

    fun onEvent(event: SearchingForGameScreenEvent) {
        when (event) {
            SearchingForGameScreenEvent.Back -> {
                CoroutineScope(Dispatchers.Default).launch {
                    withTimeoutOrNull(10.seconds) {
                        disconnect.await()()
                    }
                }
                onGoingToWelcomeScreen()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SearchingForGameScreenEvent.Back)
    }
}