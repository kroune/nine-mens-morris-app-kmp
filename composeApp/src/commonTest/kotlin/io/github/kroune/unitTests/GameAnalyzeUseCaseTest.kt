package io.github.kroune.unitTests

import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.GameAnalyzeUseCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GameAnalyzeUseCaseTest {

    private var depth = 5
    private val useCase = GameAnalyzeUseCase(
        depth = { depth },
        onDepthChange = { newDepth -> depth = newDepth }
    )

    @Test
    fun `decreaseDepth decreases depth by one`() {
        depth = 5
        useCase.decreaseDepth()
        assertEquals(4, depth)
    }

    @Test
    fun `decreaseDepth does not go below zero`() {
        depth = 0
        useCase.decreaseDepth()
        assertEquals(0, depth)
    }

    @Test
    fun `increaseDepth increases depth by one`() {
        depth = 5
        useCase.increaseDepth()
        assertEquals(6, depth)
    }

    @Test
    fun `startAnalyze returns a flow of board positions`() = runTest {
        depth = 2
        val positions = useCase.startAnalyze(gameStartPosition).toList()
        assertEquals(3, positions.size)
        assertEquals(gameStartPosition, positions[0])
        
        val move1 = gameStartPosition.findBestMove(2.toUByte())
        val pos1 = move1?.producePosition(gameStartPosition)
        assertEquals(pos1, positions[1])

        val move2 = pos1?.findBestMove(2.toUByte())
        val pos2 = move2?.producePosition(pos1)
        assertEquals(pos2, positions[2])
    }

    @Test
    fun `startAnalyze with depth 0 returns only the initial position`() = runTest {
        depth = 0
        val positions = useCase.startAnalyze(gameStartPosition).toList()
        assertEquals(1, positions.size)
        assertEquals(gameStartPosition, positions[0])
    }
}
