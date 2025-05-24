package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithFriendEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameAnalyzeUseCase
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        {
            _state.value.position
        },
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
        selectedButton = {
            _state.value.selectedButton
        },
        onSelectedButtonUpdate = { value ->
            _state.update {
                it.copy(
                    selectedButton = value
                )
            }
        }
    )

    var analyzeJob: Job? = null

    fun onEvent(event: GameWithFriendEvent) {
        when (event) {
            GameWithFriendEvent.StartAnalyze -> {
                analyzeJob?.cancel()
                analyzeJob = componentScope.launch {
                    _state.value.gameAnalyzePositions.clear()
                    gameAnalyzeUseCase.startAnalyze(_state.value.position).collect {
                        _state.value.gameAnalyzePositions.add(it)
                    }
                }
            }

            GameWithFriendEvent.DecreaseAnalyzeDepth -> {
                gameAnalyzeUseCase.decreaseDepth()
            }

            GameWithFriendEvent.IncreaseAnalyzeDepth -> {
                gameAnalyzeUseCase.increaseDepth()
            }

            is GameWithFriendEvent.OnPieceClick -> {
                with(gameUseCase) {
                    gameUseCase.onClick(event.index)
                }
            }

            GameWithFriendEvent.Redo -> {
                with(gameUseCase) {
                    gameUseCase.onRedo()
                }
            }

            GameWithFriendEvent.Undo -> {
                with(gameUseCase) {
                    gameUseCase.onUndo()
                }
            }

            GameWithFriendEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(GameWithFriendEvent.Back)
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