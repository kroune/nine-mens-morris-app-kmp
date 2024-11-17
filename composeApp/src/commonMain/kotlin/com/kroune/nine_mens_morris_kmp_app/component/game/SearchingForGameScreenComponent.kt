package com.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.interactors.searchingForGameInteractor
import com.kroune.nine_mens_morris_kmp_app.event.SearchingForGameScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchingForGameScreenComponent(
    onGameFind: (Long) -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var expectedWaitingTime = Channel<Long>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            searchingForGameInteractor.searchForGame(expectedWaitingTime)
            withContext(Dispatchers.Main) {
                onGameFind(searchingForGameInteractor.gameId!!)
            }
        }
    }

    fun onEvent(event: SearchingForGameScreenEvent) {
        when (event) {
            SearchingForGameScreenEvent.Abort -> {
                CoroutineScope(Dispatchers.Default).launch {
                    searchingForGameInteractor.disconnect()
                }
            }
        }
    }
}