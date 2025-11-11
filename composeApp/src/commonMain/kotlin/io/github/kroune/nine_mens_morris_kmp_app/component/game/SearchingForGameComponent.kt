package io.github.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.SearchingForGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame.SearchingForGameRepositoryI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchingForGameComponent(
    onGameFind: (Long) -> Unit,
    private val onGoingToWelcomeScreen: () -> Unit,
    private val searchingForGameRepository: SearchingForGameRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val scope = componentCoroutineScope()

    val searchingForGameErrorFlow = MutableSharedFlow<SearchingForGameResponse>()
    val expectedWaitingTime =
        MutableSharedFlow<Long>(10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        with(scope) {
            launch {
                val result = searchingForGameRepository.searchForGame(expectedWaitingTime)
                if (result is SearchingForGameResponse.Success) {
                    println("found game, id = ${result.gameId}")
                    withContext(Dispatchers.Main) {
                        onGameFind(result.gameId)
                    }
                }
                searchingForGameErrorFlow.emit(result)
            }
        }
    }

    fun onEvent(event: SearchingForGameScreenEvent) {
        when (event) {
            SearchingForGameScreenEvent.Back -> {
                onGoingToWelcomeScreen()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SearchingForGameScreenEvent.Back)
    }
}
