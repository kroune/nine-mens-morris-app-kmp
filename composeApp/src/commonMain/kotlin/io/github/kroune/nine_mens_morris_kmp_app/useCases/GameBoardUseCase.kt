package io.github.kroune.nine_mens_morris_kmp_app.useCases

import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nineMensMorrisLib.move.moveProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameBoardUseCase(
    /**
     * stores current position
     */
    val pos: StateFlow<Position> = MutableStateFlow(gameStartPosition),

    val onPositionChange: (Position) -> Unit,
    /**
     * stores all pieces which can be moved (used for highlighting)
     */
    val onMoveHintsUpdate: (List<Int>) -> Unit = {},
    /**
     * what we should execute on undo
     */
    val onUndo: GameBoardUseCase.() -> Unit = { this.defaultOnUndo() },
    /**
     * what we should execute on redo
     */
    val onRedo: GameBoardUseCase.() -> Unit = { this.defaultOnRedo() },
    /**
     * what will happen if we click some circle
     */
    var onClick: GameBoardUseCase.(index: Int) -> Unit = {
        val move = this.handleClick(it)
        if (move != null) {
            processMove(move)
        }
        handleHighLighting()
    },
    /**
     * used for storing info of the previous (valid one) clicked button
     */
    val onSelectedButtonUpdate: (Int?) -> Unit = {},

    val selectedButton: StateFlow<Int?>,
    /**
     * what should happen on game end
     */
    val onGameEnd: (pos: Position) -> Unit
) {

    /**
     * stores all movements (positions) history
     */
    val movesHistory: ArrayDeque<Position> = ArrayDeque()

    /**
     * stores a moves we have undone
     * resets them if we do any other move
     */
    val undoneMoveHistory: ArrayDeque<Position> = ArrayDeque()

    fun defaultOnUndo() {
        if (!movesHistory.isEmpty()) {
            undoneMoveHistory.addLast(movesHistory.last())
            movesHistory.removeLast()
            onPositionChange(movesHistory.lastOrNull() ?: gameStartPosition)
            onMoveHintsUpdate(arrayListOf())
            onSelectedButtonUpdate(null)
        }
    }

    fun defaultOnRedo() {
        if (!undoneMoveHistory.isEmpty()) {
            movesHistory.addLast(undoneMoveHistory.last())
            undoneMoveHistory.removeLast()
            onPositionChange(movesHistory.lastOrNull() ?: gameStartPosition)
            onSelectedButtonUpdate(null)
            onMoveHintsUpdate(arrayListOf())
        }
    }

    /**
     * processes selected movement
     */
    fun processMove(move: Movement) {
        onPositionChange(move.producePosition(pos.value).copy())
        onSelectedButtonUpdate(null)
        saveMove(pos.value)
        if (pos.value.gameState() == GameState.End) {
            onGameEnd(pos.value)
        }
    }

    /**
     * saves a move we have made
     */
    private fun saveMove(pos: Position) {
        if (undoneMoveHistory.isNotEmpty()) {
            undoneMoveHistory.clear()
        }
        movesHistory.addLast(pos)
    }

    /**
     * handles click on the pieces
     * @param elementIndex element that got clicked
     */
    fun handleClick(elementIndex: Int): Movement? {
        when (pos.value.gameState()) {
            GameState.Placement -> {
                if (pos.value.positions[elementIndex] == null) {
                    return Movement(null, elementIndex)
                }
            }

            GameState.Normal -> {
                if (selectedButton.value == null) {
                    if (pos.value.positions[elementIndex] == pos.value.pieceToMove) {
                        onSelectedButtonUpdate(elementIndex)
                    }
                } else {
                    if (moveProvider[selectedButton.value!!].filter { endIndex ->
                            pos.value.positions[endIndex] == null
                        }.contains(elementIndex)) {
                        return Movement(selectedButton.value, elementIndex)
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Flying -> {
                if (selectedButton.value == null) {
                    if (pos.value.positions[elementIndex] == pos.value.pieceToMove)
                        onSelectedButtonUpdate(elementIndex)
                } else {
                    if (pos.value.positions[elementIndex] == null) {
                        return Movement(selectedButton.value, elementIndex)
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Removing -> {
                if (pos.value.positions[elementIndex] == !pos.value.pieceToMove) {
                    return Movement(elementIndex, null)
                }
            }

            GameState.End -> {}
        }
        return null
    }

    /**
     * finds pieces we should highlight
     */
    fun handleHighLighting() {
        pos.value.generateMoves().let { moves ->
            when (pos.value.gameState()) {
                GameState.Placement -> {
                    onMoveHintsUpdate(moves.map { it.endIndex!! })
                }

                GameState.Normal -> {
                    if (selectedButton.value == null) {
                        onMoveHintsUpdate(moves.map { it.startIndex!! })
                    } else {
                        onMoveHintsUpdate(
                            moves
                                .filter { it.startIndex == selectedButton.value }
                                .map { it.endIndex!! }
                        )
                    }
                }

                GameState.Flying -> {
                    if (selectedButton.value == null) {
                        onMoveHintsUpdate(moves.map { it.startIndex!! })
                    } else {
                        onMoveHintsUpdate(
                            moves
                                .filter { it.startIndex == selectedButton.value }
                                .map { it.endIndex!! }
                        )
                    }
                }

                GameState.Removing -> {
                    onMoveHintsUpdate(moves.map { it.startIndex!! })
                }

                GameState.End -> {
                }
            }
        }
    }
}