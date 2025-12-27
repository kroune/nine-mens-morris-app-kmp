package io.github.kroune.nine_mens_morris_kmp_app.domain.useCases

import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement

class GameBoardUseCase(
    /**
     * stores current position
     */
    private val getPosition: () -> Position,

    private val getSelectedButton: () -> Int?,

    private val onPositionChange: (Position) -> Unit,
    /**
     * stores all pieces which can be moved (used for highlighting)
     */
    private val onMoveHintsUpdate: (Set<Int>) -> Unit = {},
    /**
     * what we should execute on undo
     */
    val onUndo: GameBoardUseCase.() -> Unit = { this.defaultOnUndo() },
    /**
     * what we should execute on redo
     */
    val onRedo: GameBoardUseCase.() -> Unit = { this.defaultOnRedo() },
    /**
     * used for storing info of the previous (valid one) clicked button
     */
    private val onSelectedButtonUpdate: (Int?) -> Unit = {},
    /**
     * what should happen on game end
     */
    private val onGameEnd: () -> Unit
) {
    private val position
        get() = getPosition()

    private val selectedButton
        get() = getSelectedButton()

    /**
     * stores all previous positions history
     */
    val pastPositionsHistory: ArrayDeque<Position> = ArrayDeque()

    /**
     * stores a positions we had during the game, but then undone them
     * resets them if we do any other move
     */
    val undonePositionsHistory: ArrayDeque<Position> = ArrayDeque()

    fun defaultOnUndo() {
        if (!pastPositionsHistory.isEmpty()) {
            undonePositionsHistory.addLast(pastPositionsHistory.last())
            pastPositionsHistory.removeLast()
            onPositionChange(pastPositionsHistory.lastOrNull() ?: gameStartPosition)
            onMoveHintsUpdate(setOf())
            onSelectedButtonUpdate(null)
        }
    }

    fun defaultOnRedo() {
        if (!undonePositionsHistory.isEmpty()) {
            pastPositionsHistory.addLast(undonePositionsHistory.last())
            undonePositionsHistory.removeLast()
            onPositionChange(pastPositionsHistory.lastOrNull() ?: gameStartPosition)
            onSelectedButtonUpdate(null)
            onMoveHintsUpdate(setOf())
        }
    }

    fun defaultOnClick(index: Int) {
        val movement = this.handleClick(index)
        if (movement != null) {
            processMovement(movement)
        }
        handleHighLighting()
    }

    /**
     * processes selected movement
     */
    fun processMovement(move: Movement) {
        val newPosition = move.producePosition(position)
        onPositionChange(newPosition)
        onSelectedButtonUpdate(null)
        savePosition(newPosition)
        if (newPosition.gameState() == GameState.End) {
            onGameEnd()
        }
    }

    /**
     * saves a move we have made
     */
    private fun savePosition(pos: Position) {
        if (undonePositionsHistory.isNotEmpty()) {
            undonePositionsHistory.clear()
        }
        pastPositionsHistory.addLast(pos)
    }

    /**
     * handles click on the pieces
     * @param elementIndex element that got clicked
     */
    fun handleClick(elementIndex: Int): Movement? {
        val possibleMoves = position.generateMoves()
        when (position.gameState()) {
            GameState.Placement -> {
                if (possibleMoves.any { it.endIndex == elementIndex }) {
                    return Movement(null, elementIndex)
                }
            }

            GameState.Normal -> {
                if (selectedButton == null) {
                    if (possibleMoves.any { it.startIndex == elementIndex }) {
                        onSelectedButtonUpdate(elementIndex)
                    }
                } else {
                    val supposedMovement = Movement(selectedButton, elementIndex)
                    if (supposedMovement in possibleMoves) {
                        return supposedMovement
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Flying -> {
                if (selectedButton == null) {
                    if (possibleMoves.any { it.startIndex == elementIndex })
                        onSelectedButtonUpdate(elementIndex)
                    else
                        onSelectedButtonUpdate(null)
                } else {
                    val supposedMovement = Movement(selectedButton, elementIndex)
                    if (supposedMovement in possibleMoves) {
                        return supposedMovement
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Removing -> {
                val supposedMovement = Movement(elementIndex, null)
                if (supposedMovement in possibleMoves) {
                    return supposedMovement
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
        val possibleMoves = position.generateMoves()
        when (position.gameState()) {
            GameState.Placement -> {
                onMoveHintsUpdate(possibleMoves.map { it.endIndex!! }.toSet())
            }

            GameState.Normal -> {
                if (selectedButton == null) {
                    onMoveHintsUpdate(possibleMoves.map { it.startIndex!! }.toSet())
                } else {
                    onMoveHintsUpdate(
                        possibleMoves
                            .filter { it.startIndex == selectedButton }
                            .map { it.endIndex!! }
                            .toSet()
                    )
                }
            }

            GameState.Flying -> {
                if (selectedButton == null) {
                    onMoveHintsUpdate(possibleMoves.map { it.startIndex!! }.toSet())
                } else {
                    onMoveHintsUpdate(
                        possibleMoves
                            .filter { it.startIndex == selectedButton }
                            .map { it.endIndex!! }
                            .toSet()
                    )
                }
            }

            GameState.Removing -> {
                onMoveHintsUpdate(possibleMoves.map { it.startIndex!! }.toSet())
            }

            GameState.End -> {
                onMoveHintsUpdate(setOf())
            }
        }
    }
}
