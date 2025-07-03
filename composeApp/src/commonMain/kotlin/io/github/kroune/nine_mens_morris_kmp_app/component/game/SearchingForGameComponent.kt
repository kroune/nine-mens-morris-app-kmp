package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.SearchingForGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game.GameRepositoryI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchingForGameComponent(
    onGameFind: (Long) -> Unit,
    private val onGoingToWelcomeScreen: () -> Unit,
    private val gameRepository: GameRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val scope = componentCoroutineScope()

    private val _state = MutableStateFlow(
        SearchingForGameScreenState(
            searchingForGameError = null,
            expectedWaitingTime = null
        )
    )
    val state
        get() = _state

    init {
        with(scope) {
            val expectedWaitingTimeChannel = Channel<Long>(10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            launch {
                expectedWaitingTimeChannel.consumeEach { expectedWaitingTime ->
                    _state.update {
                        it.copy(
                            expectedWaitingTime = expectedWaitingTime
                        )
                    }
                }
            }
            launch {
                val result = gameRepository.searchForGame(expectedWaitingTimeChannel)
                if (result is SearchingForGameResponse.Success) {
                    println("found game, id = ${result.gameId}")
                    withContext(Dispatchers.Main) {
                        onGameFind(result.gameId)
                    }
                }
                _state.update {
                    it.copy(
                        searchingForGameError = result
                    )
                }
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

@Immutable
data class SearchingForGameScreenState(
    val searchingForGameError: SearchingForGameResponse?,
    val expectedWaitingTime: Long?
)