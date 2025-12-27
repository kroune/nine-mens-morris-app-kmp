package io.github.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.SearchingForGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.log
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.searchingForGame.SearchingForGameRepositoryI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchingForGameComponent(
    onGameFind: (Long) -> Unit,
    private val onGoingToWelcomeScreen: () -> Unit,
    private val searchingForGameRepository: SearchingForGameRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val scope = componentCoroutineScope()

    private val _searchingForGameErrorFlow = MutableSharedFlow<SearchingForGameEvent.Error>()
    val searchingForGameErrorFlow
        get() = _searchingForGameErrorFlow.asSharedFlow()

    private val _expectedWaitingTime =
        MutableStateFlow(SearchingForGameScreenState(null))
    val expectedWaitingTime
        get() = _expectedWaitingTime.asStateFlow()

    init {
        scope.launch {
            searchingForGameRepository.searchForGame().collect { result ->
                if (result !is SearchingForGameEvent.Error) {
                    when (result) {
                        is SearchingForGameEvent.Success.GameFound -> {
                            log("found game, id = ${result.gameId}", severity = Severity.INFO)
                            withContext(Dispatchers.Main) {
                                onGameFind(result.gameId)
                            }
                            cancel()
                        }

                        is SearchingForGameEvent.Success.NewExpectedWaitingTime -> {
                            _expectedWaitingTime.update {
                                it.copy(
                                    expectedWaitingTime = result.expectedWaitingTime,
                                )
                            }
                        }
                    }
                } else {
                    _searchingForGameErrorFlow.emit(result)
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

data class SearchingForGameScreenState(
    val expectedWaitingTime: Long?,
)
