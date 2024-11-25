package com.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.event.SearchingForGameScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGameInteractor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class SearchingForGameScreenComponent(
    onGameFind: (Long) -> Unit,
    onGoingToWelcomeScreen: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var expectedWaitingTime = Channel<Long>()

    private val disconnect: CompletableDeferred<suspend () -> Unit> = CompletableDeferred()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val (gameId, onClose) = searchingForGameInteractor.searchForGame(expectedWaitingTime)
            disconnect.complete {
                println("searching for game is closing")
                onClose()
            }
            gameId.await()!!.let { gameIdResult ->
                gameIdResult.onFailure {
                    if (it is CancellationException)
                        // that's ok
                        return@let
                    // TODO: log error
                    it.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onGoingToWelcomeScreen()
                    }
                }
                gameIdResult.onSuccess {
                    println("found game, id = $it")
                    withContext(Dispatchers.Main) {
                        onGameFind(it)
                    }
                }
            }
        }
    }

    fun onEvent(event: SearchingForGameScreenEvent) {
        when (event) {
            SearchingForGameScreenEvent.Abort -> {
                CoroutineScope(Dispatchers.Default).launch {
                    disconnect.await()()
                }
            }
        }
    }
}