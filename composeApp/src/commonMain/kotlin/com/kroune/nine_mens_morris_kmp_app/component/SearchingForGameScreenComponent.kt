package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.searchingForGameInteractor
import com.kroune.nine_mens_morris_kmp_app.event.SearchingForGameScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class SearchingForGameScreenComponent(
    onGameFind: (Long) -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var expectedWaitingTime by mutableStateOf<Long?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            searchingForGameInteractor.searchForGame()
        }
        CoroutineScope(Dispatchers.Default).launch {
            searchingForGameInteractor.channel.consumeEach {
                expectedWaitingTime = it
            }
            onGameFind(searchingForGameInteractor.gameId!!)
        }
    }

    fun onEvent(event: SearchingForGameScreenEvent) {
        when (event) {
            SearchingForGameScreenEvent.Abort -> {
                searchingForGameInteractor.channel
            }
        }
    }
}