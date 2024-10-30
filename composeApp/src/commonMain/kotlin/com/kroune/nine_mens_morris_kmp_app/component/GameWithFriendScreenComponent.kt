package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nine_mens_morris_kmp_app.event.GameWithFriendEvent
import com.kroune.nine_mens_morris_kmp_app.useCases.GameAnalyzeUseCase
import com.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase

class GameWithFriendScreenComponent(
    private val onGameEnd: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val gameAnalyzeUseCase = GameAnalyzeUseCase()
    private val gameUseCase = GameBoardUseCase(
        mutableStateOf(gameStartPosition),
        onGameEnd = {
            onGameEnd()
        }
    )

    val position by gameUseCase.pos
    val selectedButton by gameUseCase.selectedButton
    val moveHints by gameUseCase.moveHints
    val gameAnalyzePositions = gameAnalyzeUseCase.positionsValue
    val analyzeDepth by gameAnalyzeUseCase.depthValue

    fun onEvent(event: GameWithFriendEvent) {
        when (event) {
            GameWithFriendEvent.StartAnalyze -> {
                gameAnalyzeUseCase.startAnalyze(position)
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
        }
    }
}