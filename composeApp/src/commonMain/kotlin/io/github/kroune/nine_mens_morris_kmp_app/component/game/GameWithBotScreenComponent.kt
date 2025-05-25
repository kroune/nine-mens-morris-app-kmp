package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithBotEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameWithBotScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

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

    private fun onClick(index: Int) {
        if (_state.value.position.pieceToMove) {
            val move = gameUseCase.handleClick(index)
            if (move != null) {
                gameUseCase.processMove(move)
            }
            gameUseCase.handleHighLighting()
            botJob = componentScope.launch {
                while (gameUseCase.canBotMove()) {
                    gameUseCase.botMove()
                    gameUseCase.handleHighLighting()
                }
            }
        }
    }

    private val gameUseCase = GameBoardUseCase(
        { _state.value.position },
        onPositionChange = { value ->
            _state.update {
                it.copy(
                    position = value
                )
            }
        },
        onUndo = {
            defaultOnUndo()
            botJob?.cancel()
            botJob = componentScope.launch {
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
                botJob = componentScope.launch {
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
                    onClick(event.index)
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

    private fun GameBoardUseCase.canBotMove(): Boolean {
        val position = _state.value.position
        val canMove = position.generateMoves()
            .isNotEmpty()
        return !position.pieceToMove && position.gameState() != GameState.End && canMove
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