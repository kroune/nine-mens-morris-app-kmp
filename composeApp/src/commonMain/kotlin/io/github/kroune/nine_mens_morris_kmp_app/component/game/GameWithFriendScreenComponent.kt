package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.GameWithFriendEvent
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameAnalyzeUseCase
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class GameWithFriendScreenComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    val selectedButton = MutableStateFlow<Int?>(null)
    var moveHints by mutableStateOf(listOf<Int>())

    val position = MutableStateFlow(gameStartPosition)

    private val gameAnalyzeUseCase = GameAnalyzeUseCase()
    private val gameUseCase = GameBoardUseCase(
        position,
        onPositionChange = {
            position.value = it
        },
        onGameEnd = {
            gameEnded = true
        },
        onMoveHintsUpdate = {
            moveHints = it
        },
        selectedButton = selectedButton,
        onSelectedButtonUpdate = {
            selectedButton.value = it
        }
    )

    val gameAnalyzePositions = gameAnalyzeUseCase.positionsValue
    val analyzeDepth by gameAnalyzeUseCase.depthValue
    var gameEnded by mutableStateOf(false)

    fun onEvent(event: GameWithFriendEvent) {
        when (event) {
            GameWithFriendEvent.StartAnalyze -> {
                gameAnalyzeUseCase.startAnalyze(position.value)
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