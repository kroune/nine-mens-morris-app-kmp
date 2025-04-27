package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithBotEvent
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GameWithBotScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {

    private var botJob: Job? = null

    val selectedButton = MutableStateFlow<Int?>(null)
    var moveHints by mutableStateOf(listOf<Int>())

    val position = MutableStateFlow(gameStartPosition)
    var gameEnded by mutableStateOf(false)
    private val gameUseCase = GameBoardUseCase(
        position,
        onPositionChange = {
            position.value = it
        },
        onClick = { index ->
            if (pos.value.pieceToMove) {
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
            if (pos.value.pieceToMove) {
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
        onMoveHintsUpdate = {
            moveHints = it
        },
        onSelectedButtonUpdate = {
            selectedButton.value = it
        },
        selectedButton = selectedButton,
        onGameEnd = {
            gameEnded = true
        }
    )

    private fun GameBoardUseCase.botMove() {
        val bestMove = pos.value.findBestMove(4u)
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
        return !pos.value.pieceToMove && pos.value.gameState() != GameState.End && pos.value.generateMoves()
            .isNotEmpty()
    }

    override fun onBackPressed() {
        onEvent(GameWithBotEvent.Back)
    }
}