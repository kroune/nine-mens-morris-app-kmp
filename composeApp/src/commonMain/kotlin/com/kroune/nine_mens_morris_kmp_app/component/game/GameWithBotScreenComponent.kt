package com.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.event.GameWithBotEvent
import com.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameWithBotScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {

    private var botJob: Job? = null

    var gameEnded by mutableStateOf(false)
    private val gameUseCase = GameBoardUseCase(
        mutableStateOf(gameStartPosition),
        onClick = { index ->
            if (pos.value.pieceToMove) {
                val move = this.handleClick(index)
                if (move != null) {
                    processMove(move)
                }
                handleHighLighting()
                botJob = CoroutineScope(Dispatchers.Default).launch {
                    while (!pos.value.pieceToMove && pos.value.gameState() != GameState.End) {
                        botMove()
                    }
                }
            }
        },
        onUndo = {
            defaultOnUndo()
            botJob?.cancel()
            botJob = CoroutineScope(Dispatchers.Default).launch {
                delay(800)
                while (!pos.value.pieceToMove && pos.value.gameState() != GameState.End) {
                    botMove()
                }
            }
        },
        onRedo = {
            if (pos.value.pieceToMove) {
                defaultOnRedo()
                botJob?.cancel()
                botJob = CoroutineScope(Dispatchers.Default).launch {
                    delay(800)
                    while (!pos.value.pieceToMove && pos.value.gameState() != GameState.End) {
                        botMove()
                    }
                }
            }
        },
        onGameEnd = {
            gameEnded = true
        }
    )

    fun GameBoardUseCase.botMove() {
        val bestMove = pos.value.findBestMove(4u)
        processMove(bestMove!!)
    }

    val position by gameUseCase.pos
    val selectedButton by gameUseCase.selectedButton
    val moveHints by gameUseCase.moveHints
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

    override fun onBackPressed() {
        onEvent(GameWithBotEvent.Back)
    }
}