package io.github.kroune.nine_mens_morris_kmp_app.useCases

import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nineMensMorrisLib.move.moveProvider

class GameBoardUseCase(
    /**
     * stores current position
     */
    val pos: () -> Position,

    val onPositionChange: (Position) -> Unit,
    /**
     * stores all pieces which can be moved (used for highlighting)
     */
    val onMoveHintsUpdate: (Set<Int>) -> Unit = {},
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
    val onSelectedButtonUpdate: (Int?) -> Unit = {},

    val selectedButton: () -> Int?,
    /**
     * what should happen on game end
     */
    val onGameEnd: () -> Unit
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
            onMoveHintsUpdate(setOf())
            onSelectedButtonUpdate(null)
        }
    }

    fun defaultOnRedo() {
        if (!undoneMoveHistory.isEmpty()) {
            movesHistory.addLast(undoneMoveHistory.last())
            undoneMoveHistory.removeLast()
            onPositionChange(movesHistory.lastOrNull() ?: gameStartPosition)
            onSelectedButtonUpdate(null)
            onMoveHintsUpdate(setOf())
        }
    }

    fun defaultOnClick(index: Int) {
        val move = this.handleClick(index)
        if (move != null) {
            processMove(move)
        }
        handleHighLighting()
    }
    /**
     * processes selected movement
     */
    fun processMove(move: Movement) {
        val newPosition = move.producePosition(pos()).copy()
        onPositionChange(newPosition)
        onSelectedButtonUpdate(null)
        saveMove(newPosition)
        if (newPosition.gameState() == GameState.End || newPosition.generateMoves().isEmpty()) {
            onGameEnd()
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
        when (pos().gameState()) {
            GameState.Placement -> {
                if (pos().positions[elementIndex] == null) {
                    return Movement(null, elementIndex)
                }
            }

            GameState.Normal -> {
                if (selectedButton() == null) {
                    if (pos().positions[elementIndex] == pos().pieceToMove) {
                        onSelectedButtonUpdate(elementIndex)
                    }
                } else {
                    if (moveProvider[selectedButton()!!].filter { endIndex ->
                            pos().positions[endIndex] == null
                        }.contains(elementIndex)) {
                        return Movement(selectedButton(), elementIndex)
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Flying -> {
                if (selectedButton() == null) {
                    if (pos().positions[elementIndex] == pos().pieceToMove)
                        onSelectedButtonUpdate(elementIndex)
                } else {
                    if (pos().positions[elementIndex] == null) {
                        return Movement(selectedButton(), elementIndex)
                    } else {
                        onSelectedButtonUpdate(null)
                    }
                }
            }

            GameState.Removing -> {
                if (pos().positions[elementIndex] == !pos().pieceToMove) {
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
        val position = pos()
        position.generateMoves().let { moves ->
            when (pos().gameState()) {
                GameState.Placement -> {
                    onMoveHintsUpdate(moves.map { it.endIndex!! }.toSet())
                }

                GameState.Normal -> {
                    if (selectedButton() == null) {
                        onMoveHintsUpdate(moves.map { it.startIndex!! }.toSet())
                    } else {
                        onMoveHintsUpdate(
                            moves
                                .filter { it.startIndex == selectedButton() }
                                .map { it.endIndex!! }
                                .toSet()
                        )
                    }
                }

                GameState.Flying -> {
                    if (selectedButton() == null) {
                        onMoveHintsUpdate(moves.map { it.startIndex!! }.toSet())
                    } else {
                        onMoveHintsUpdate(
                            moves
                                .filter { it.startIndex == selectedButton() }
                                .map { it.endIndex!! }
                                .toSet()
                        )
                    }
                }

                GameState.Removing -> {
                    onMoveHintsUpdate(moves.map { it.startIndex!! }.toSet())
                }

                GameState.End -> {
                }
            }
        }
    }
}