package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithBotEvent
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameWithBotScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val _state = MutableStateFlow(
        GameWithBotScreenState(
            gameStartPosition,
            setOf(),
            null,
            false
        )
    )
    val state
        get() = _state

    private var botJob: Job? = null

    private val gameUseCase = GameBoardUseCase(
        { _state.value.position },
        onPositionChange = { value ->
            _state.update {
                it.copy(
                    position = value
                )
            }
        },
        onClick = { index ->
            if (_state.value.position.pieceToMove) {
                val move = this.handleClick(index)
                if (move != null) {
                    processMove(move)
                }
                handleHighLighting()
                botJob = CoroutineScope(Dispatchers.Default).launch {
                    while (canBotMove()) {
                        botMove()
                        handleHighLighting()
                    }
                }
            }
        },
        onUndo = {
            defaultOnUndo()
            botJob?.cancel()
            botJob = CoroutineScope(Dispatchers.Default).launch {
                delay(800)
                while (canBotMove()) {
                    botMove()
                    handleHighLighting()
                }
            }
        },
        onRedo = {
            if (_state.value.position.pieceToMove) {
                defaultOnRedo()
                botJob?.cancel()
                botJob = CoroutineScope(Dispatchers.Default).launch {
                    delay(800)
                    while (canBotMove()) {
                        botMove()
                        handleHighLighting()
                    }
                }
            }
        },
        onMoveHintsUpdate = { value ->
            _state.update {
                it.copy(
                    moveHints = value
                )
            }
        },
        onSelectedButtonUpdate = { value ->
            _state.update {
                it.copy(
                    selectedButton = value
                )
            }
        },
        selectedButton = {
            _state.value.selectedButton
        },
        onGameEnd = {
            _state.update {
                it.copy(
                    gameEnded = true
                )
            }
        }
    )

    private fun GameBoardUseCase.botMove() {
        val bestMove = _state.value.position.findBestMove(4u)
        processMove(bestMove!!)
    }

    fun onEvent(event: GameWithBotEvent) {
        when (event) {
            is GameWithBotEvent.OnPieceClick -> {
                with(gameUseCase) {
                    gameUseCase.onClick(event.index)
                }
            }

            GameWithBotEvent.Redo -> {
                with(gameUseCase) {
                    gameUseCase.onRedo()
                }
            }

            GameWithBotEvent.Undo -> {
                with(gameUseCase) {
                    gameUseCase.onUndo()
                }
            }

            GameWithBotEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    fun GameBoardUseCase.canBotMove(): Boolean {
        return !_state.value.position.pieceToMove && _state.value.position.gameState() != GameState.End && _state.value.position.generateMoves()
            .isNotEmpty()
    }

    override fun onBackPressed() {
        onEvent(GameWithBotEvent.Back)
    }
}

@Immutable
data class GameWithBotScreenState(
    val position: Position,
    val moveHints: Set<Int>,
    val selectedButton: Int?,
    val gameEnded: Boolean
)