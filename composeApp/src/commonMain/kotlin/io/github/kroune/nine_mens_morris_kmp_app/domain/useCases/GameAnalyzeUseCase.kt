package io.github.kroune.nine_mens_morris_kmp_app.domain.useCases

import com.kroune.nineMensMorrisLib.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext
import kotlin.math.max

/**
 * game analyze use case
 * uses local analysis
 */
class GameAnalyzeUseCase(
    val depth: () -> Int,
    val onDepthChange: (Int) -> Unit
) {
    /**
     * decreases search depth
     */
    fun decreaseDepth() {
        onDepthChange(
            max(0, depth() - 1)
        )
        stopAnalyze()
    }

    /**
     * increases search depth
     */
    fun increaseDepth() {
        onDepthChange(
            depth() + 1
        )
        stopAnalyze()
    }

    /**
     * current analyze job
     */
    var analyzeJob: Job? = null

    /**
     * starts board analyze
     */
    fun startAnalyze(pos: Position): Flow<Position> {
        return flow<Position> {
            var currentPos = pos
            emit(currentPos)
            repeat(depth()) {
                coroutineContext.ensureActive()
                val move = currentPos.findBestMove(depth().toUByte()) ?: return@flow
                currentPos = move.producePosition(currentPos)
                emit(currentPos)
            }
        }
    }

    /**
     * hides analyze gui and delete it's result
     */
    private fun stopAnalyze() {
        analyzeJob?.cancel()
    }
}
