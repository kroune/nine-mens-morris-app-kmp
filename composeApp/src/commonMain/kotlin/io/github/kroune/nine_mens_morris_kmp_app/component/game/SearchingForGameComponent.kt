package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.SearchingForGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game.GameRepositoryI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchingForGameComponent(
    onGameFind: (Long) -> Unit,
    private val onGoingToWelcomeScreen: () -> Unit,
    private val gameRepository: GameRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val scope = componentCoroutineScope()

    val searchingForGameError: MutableState<SearchingForGameResponse?> = mutableStateOf(null)
    val expectedWaitingTime = Channel<Long>(10, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        with(scope) {
            launch {
                val result = gameRepository.searchForGame(expectedWaitingTime)
                if (result is SearchingForGameResponse.Success) {
                    println("found game, id = ${result.gameId}")
                    withContext(Dispatchers.Main) {
                        onGameFind(result.gameId)
                    }
                }
                searchingForGameError.value = result
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
