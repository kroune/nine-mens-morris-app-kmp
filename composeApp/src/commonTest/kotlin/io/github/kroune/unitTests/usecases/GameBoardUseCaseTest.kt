package io.github.kroune.unitTests.usecases

import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.GameState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.gameStartPosition
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.GameBoardUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameBoardUseCaseTest {

    private lateinit var useCase: GameBoardUseCase
    private var position: Position = gameStartPosition
    private var selectedButton: Int? = null
    private var moveHints = setOf<Int>()
    private var gameEndCalled = false

    @BeforeTest
    fun setUp() {
        position = gameStartPosition
        selectedButton = null
        moveHints = setOf()
        gameEndCalled = false
    }

    private fun initializeUseCase(initialPosition: Position = position) {
        position = initialPosition
        useCase = GameBoardUseCase(
            getPosition = { position },
            getSelectedButton = { selectedButton },
            onPositionChange = { position = it },
            onSelectedButtonUpdate = { selectedButton = it },
            onMoveHintsUpdate = { moveHints = it },
            onGameEnd = { gameEndCalled = true },
        )
    }

    private fun createNormalPhasePosition(): Position {
        var tempPosition = gameStartPosition
        var moveCount = 0
        while (tempPosition.gameState() == GameState.Placement && moveCount < 100) {
            val moves = tempPosition.generateMoves()
            if (moves.isEmpty()) break
            tempPosition = moves.first().producePosition(tempPosition)
            moveCount++
        }
        return tempPosition
    }

    private fun createRemovingPhasePosition(): Position {
        var tempPosition = gameStartPosition
        var moveCount = 0
        while (tempPosition.gameState() != GameState.Removing && tempPosition.gameState() != GameState.End && moveCount < 200) {
            val moves = tempPosition.generateMoves()
            if (moves.isEmpty()) break
            tempPosition = moves.first().producePosition(tempPosition)
            moveCount++
        }
        return tempPosition
    }

    @Test
    fun `handleClick on valid empty spot during Placement phase returns a placement Movement`() {
        initializeUseCase()
        val possibleMoves = position.generateMoves()
        val firstMoveIndex = possibleMoves.first().endIndex!!

        val movement = useCase.handleClick(firstMoveIndex)

        assertNotNull(movement)
        assertEquals(null, movement.startIndex)
        assertEquals(firstMoveIndex, movement.endIndex)
    }

    @Test
    fun `handleClick on invalid spot during Placement phase returns null`() {
        initializeUseCase()
        val movement = useCase.handleClick(100)
        assertNull(movement)
    }

    @Test
    fun `handleClick on own piece during Normal phase selects the piece`() {
        val normalPhasePosition = createNormalPhasePosition()
        if (normalPhasePosition.gameState() !in listOf(GameState.Normal, GameState.Flying)) return
        initializeUseCase(normalPhasePosition)

        val firstPieceIndex = position.generateMoves().first().startIndex!!
        val movement = useCase.handleClick(firstPieceIndex)

        assertNull(movement)
        assertEquals(firstPieceIndex, selectedButton)
    }

    @Test
    fun `handleClick on valid destination during Normal phase with a piece selected completes the move`() {
        val normalPhasePosition = createNormalPhasePosition()
        if (normalPhasePosition.gameState() !in listOf(GameState.Normal, GameState.Flying)) return
        initializeUseCase(normalPhasePosition)

        val firstMove = position.generateMoves().first()
        selectedButton = firstMove.startIndex

        val movement = useCase.handleClick(firstMove.endIndex!!)

        assertNotNull(movement)
        assertEquals(firstMove.startIndex, movement.startIndex)
        assertEquals(firstMove.endIndex, movement.endIndex)
    }

    @Test
    fun `handleClick on removable opponent piece during Removing phase returns a removal Movement`() {
        val removingPhasePosition = createRemovingPhasePosition()

        if (removingPhasePosition.gameState() == GameState.Removing) {
            initializeUseCase(removingPhasePosition)
            val removeIndex = position.generateMoves().first().startIndex!!
            val movement = useCase.handleClick(removeIndex)

            assertNotNull(movement)
            assertEquals(removeIndex, movement.startIndex)
            assertNull(movement.endIndex)
        }
    }

    @Test
    fun `processMovement updates position, adds to history, and clears undone history`() {
        initializeUseCase()
        useCase.undonePositionsHistory.addLast(gameStartPosition)
        val initialHistorySize = useCase.pastPositionsHistory.size
        val movement = position.generateMoves().first()

        useCase.processMovement(movement)

        assertEquals(initialHistorySize + 1, useCase.pastPositionsHistory.size)
        assertEquals(movement.producePosition(gameStartPosition), position)
        assertTrue(useCase.undonePositionsHistory.isEmpty())
    }

    @Test
    fun `processMovement calls onGameEnd when a move results in a win`() {
        // @formatter:off
        val endPositionSetup = Position(
            positions = arrayOf(
                GREEN,                  GREEN,                  EMPTY,
                        BLUE_,          EMPTY,          GREEN,
                                EMPTY,  EMPTY,  EMPTY,
                EMPTY,  EMPTY,  BLUE_,          EMPTY,  EMPTY, EMPTY,
                                EMPTY,  GREEN,  EMPTY,
                        GREEN,          GREEN,          GREEN,
                BLUE_,                  EMPTY,                  EMPTY
            ),
            freeGreenPieces = 0u,
            freeBluePieces = 0u,
            pieceToMove = true,
            removalCount = 0u,
        )
        // @formatter:on
        initializeUseCase(endPositionSetup)
        val positions = endPositionSetup.positions.clone()
        positions[16] = BLUE_
        positions[11] = EMPTY
        val millMove = Movement(5, 2)
        useCase.processMovement(millMove)
        assertEquals(GameState.Removing, position.gameState())
        assertFalse(gameEndCalled)
        val removalMove = Movement(3, null)
        useCase.processMovement(removalMove)
        assertEquals(GameState.End, position.gameState())
        assertTrue(gameEndCalled)
    }

    @Test
    fun `defaultOnUndo reverts to the previous position and adds to undone history`() {
        initializeUseCase()
        val movement = position.generateMoves().first()
        useCase.processMovement(movement)
        val newPosition = position

        useCase.defaultOnUndo()

        assertEquals(gameStartPosition, position)
        assertEquals(newPosition, useCase.undonePositionsHistory.last())
    }

    @Test
    fun `defaultOnRedo restores an undone position`() {
        initializeUseCase()
        val movement = position.generateMoves().first()
        useCase.processMovement(movement)
        val newPosition = position
        useCase.defaultOnUndo()

        useCase.defaultOnRedo()

        assertEquals(newPosition, position)
    }

    @Test
    fun `defaultOnUndo does nothing if history is empty`() {
        initializeUseCase()
        useCase.defaultOnUndo()
        assertEquals(gameStartPosition, position)
        assertTrue(useCase.pastPositionsHistory.isEmpty())
    }

    @Test
    fun `defaultOnRedo does nothing if undone history is empty`() {
        initializeUseCase()
        useCase.defaultOnRedo()
        assertEquals(gameStartPosition, position)
        assertTrue(useCase.undonePositionsHistory.isEmpty())
    }

    @Test
    fun `handleHighLighting shows all empty spots during Placement phase`() {
        initializeUseCase()
        useCase.handleHighLighting()
        val expectedHints = position.generateMoves().map { it.endIndex!! }.toSet()
        assertEquals(expectedHints, moveHints)
    }

    @Test
    fun `handleHighLighting shows movable pieces during Normal phase when none selected`() {
        val normalPhasePosition = createNormalPhasePosition()
        if (normalPhasePosition.gameState() !in listOf(GameState.Normal, GameState.Flying)) return
        initializeUseCase(normalPhasePosition)

        useCase.handleHighLighting()

        val expectedHints = position.generateMoves().map { it.startIndex!! }.toSet()
        assertEquals(expectedHints, moveHints)
    }

    @Test
    fun `handleHighLighting shows valid destinations during Normal phase when a piece is selected`() {
        // @formatter:off
        val testPosition = Position(
            positions = arrayOf(
                GREEN,                  EMPTY,                  EMPTY,
                        EMPTY,          EMPTY,          EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                EMPTY,  EMPTY,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                        EMPTY,          EMPTY,          EMPTY,
                EMPTY,                  EMPTY,                  EMPTY
            ),
            freeGreenPieces = 0u,
            freeBluePieces = 0u,
            pieceToMove = true,
            removalCount = 0u,
        )
        // @formatter:on
        initializeUseCase(testPosition)
        selectedButton = 0

        useCase.handleHighLighting()

        val expectedHints = position.generateMoves()
            .filter { it.startIndex == selectedButton }
            .map { it.endIndex!! }
            .toSet()
        assertEquals(expectedHints, moveHints)
    }

    @Test
    fun `handleHighLighting shows removable opponent pieces during Removing phase`() {
        // @formatter:off
        val removingPosition = Position(
            positions = arrayOf(
                GREEN,                  GREEN,                  GREEN,
                        EMPTY,          EMPTY,          EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                EMPTY,  EMPTY,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                        EMPTY,          BLUE_,          EMPTY,
                EMPTY,                  EMPTY,                  EMPTY
            ),
            freeGreenPieces = 0u,
            freeBluePieces = 0u,
            pieceToMove = true,
            removalCount = 1u,
        )
        // @formatter:on
        initializeUseCase(removingPosition)
        useCase.handleHighLighting()

        val expectedHints = position.generateMoves().map { it.startIndex!! }.toSet()
        assertEquals(expectedHints, moveHints)
    }

    @Test
    fun `defaultOnClick performs a full placement and highlight cycle`() {
        initializeUseCase()
        val validIndex = position.generateMoves().first().endIndex!!

        useCase.defaultOnClick(validIndex)

        assertNotEquals(gameStartPosition, position)
        assertEquals(1, useCase.pastPositionsHistory.size)
    }
}
