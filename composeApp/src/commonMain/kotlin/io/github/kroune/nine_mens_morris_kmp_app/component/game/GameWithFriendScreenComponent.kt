package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.event.game.GameWithFriendScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameAnalyzeUseCase
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

class GameWithFriendScreenComponent(
    private val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    private val _state = MutableStateFlow(
        GameWithFriendScreenState(
            gameStartPosition,
            setOf(),
            null,
            4,
            false,
            mutableStateListOf()
        )
    )
    val state: StateFlow<GameWithFriendScreenState>
        get() = _state

    private val gameAnalyzeUseCase = GameAnalyzeUseCase(
        depth = {
            _state.value.depth
        },
        onDepthChange = { value ->
            _state.update {
                it.copy(
                    depth = value
                )
            }
        }
    )

    private val gameUseCase = GameBoardUseCase(
        { _state.value.position },
        onPositionChange = { value ->
            _state.update {
                it.copy(
                    position = value
                )
            }
        },
        onGameEnd = {
            _state.update {
                it.copy(
                    gameEnded = true
                )
            }
        },
        onMoveHintsUpdate = { value ->
            _state.update {
                it.copy(
                    moveHints = value
                )
            }
        },
        selectedButton = { _state.value.selectedButton },
        onSelectedButtonUpdate = { value ->
            _state.update {
                it.copy(
                    selectedButton = value
                )
            }
        }
    )

    @Volatile
    var analyzeJob: Job? = null
    val analyzeJobLock = Mutex()

    fun onGameAnalyzeEvent(event: GameWithFriendScreenEvent.GameAnalyzeEvent) {
        when (event) {
            GameWithFriendScreenEvent.GameAnalyzeEvent.DecreaseAnalyzeDepth -> {
                gameAnalyzeUseCase.decreaseDepth()
            }

            GameWithFriendScreenEvent.GameAnalyzeEvent.IncreaseAnalyzeDepth -> {
                gameAnalyzeUseCase.increaseDepth()
            }

            GameWithFriendScreenEvent.GameAnalyzeEvent.StartAnalyze -> {
                if (analyzeJob?.isActive == true)
                    return
                // double check lock
                componentScope.launch {
                    analyzeJobLock.withLock {
                        if (analyzeJob?.isActive == true)
                            return@launch

                        analyzeJob = launch {
                            _state.value.gameAnalyzePositions.clear()
                            gameAnalyzeUseCase.startAnalyze(_state.value.position).collect {
                                _state.value.gameAnalyzePositions.add(it)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: GameWithFriendScreenEvent) {
        when (event) {
            is GameWithFriendScreenEvent.GameAnalyzeEvent -> {
                onGameAnalyzeEvent(event)
            }

            is GameWithFriendScreenEvent.OnPieceClick -> {
                with(gameUseCase) {
                    gameUseCase.defaultOnClick(event.index)
                }
            }

            GameWithFriendScreenEvent.Redo -> {
                with(gameUseCase) {
                    gameUseCase.onRedo()
                }
            }

            GameWithFriendScreenEvent.Undo -> {
                with(gameUseCase) {
                    gameUseCase.onUndo()
                }
            }

            GameWithFriendScreenEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(GameWithFriendScreenEvent.Back)
    }
}


@Immutable
data class GameWithFriendScreenState(
    val position: Position,
    val moveHints: Set<Int>,
    val selectedButton: Int?,

    val depth: Int,
    val gameEnded: Boolean,
    val gameAnalyzePositions: SnapshotStateList<Position>
)
