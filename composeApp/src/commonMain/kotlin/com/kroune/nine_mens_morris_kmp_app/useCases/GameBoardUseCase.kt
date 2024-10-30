package com.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nineMensMorrisLib.move.moveProvider

class GameBoardUseCase(
    /**
     * stores current position
     */
    val pos: MutableState<Position> = mutableStateOf(gameStartPosition),
    /**
     * stores all pieces which can be moved (used for highlighting)
     */
    val moveHints: MutableState<List<Int>> = mutableStateOf(listOf()),
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
    val selectedButton: MutableState<Int?> = mutableStateOf(null),
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
            pos.value = movesHistory.lastOrNull() ?: gameStartPosition
            moveHints.value = arrayListOf()
            selectedButton.value = null
        }
    }

    fun defaultOnRedo() {
        if (!undoneMoveHistory.isEmpty()) {
            movesHistory.addLast(undoneMoveHistory.last())
            undoneMoveHistory.removeLast()
            pos.value = movesHistory.lastOrNull() ?: gameStartPosition
            selectedButton.value = null
            moveHints.value = arrayListOf()
        }
    }

    /**
     * processes selected movement
     */
    fun processMove(move: Movement) {
        pos.value = move.producePosition(pos.value).copy()
        selectedButton.value = null
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
                        selectedButton.value = elementIndex
                    }
                } else {
                    if (moveProvider[selectedButton.value!!].filter { endIndex ->
                            pos.value.positions[endIndex] == null
                        }.contains(elementIndex)) {
                        return Movement(selectedButton.value, elementIndex)
                    } else {
                        selectedButton.value = null
                    }
                }
            }

            GameState.Flying -> {
                if (selectedButton.value == null) {
                    if (pos.value.positions[elementIndex] == pos.value.pieceToMove)
                        selectedButton.value = elementIndex
                } else {
                    if (pos.value.positions[elementIndex] == null) {
                        return Movement(selectedButton.value, elementIndex)
                    } else {
                        selectedButton.value = null
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
                    moveHints.value = moves.map { it.endIndex!! }.toMutableList()
                }

                GameState.Normal -> {
                    if (selectedButton.value == null) {
                        moveHints.value = moves.map { it.startIndex!! }.toMutableList()
                    } else {
                        moveHints.value = moves.filter { it.startIndex == selectedButton.value }
                            .map { it.endIndex!! }.toMutableList()
                    }
                }

                GameState.Flying -> {
                    if (selectedButton.value == null) {
                        moveHints.value = moves.map { it.startIndex!! }.toMutableList()
                    } else {
                        moveHints.value = moves.filter { it.startIndex == selectedButton.value }
                            .map { it.endIndex!! }.toMutableList()
                    }
                }

                GameState.Removing -> {
                    moveHints.value = moves.map { it.startIndex!! }.toMutableList()
                }

                GameState.End -> {
                }
            }
        }
    }
}