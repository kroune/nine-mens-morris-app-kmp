package io.github.kroune.nine_mens_morris_kmp_app.domain.useCases

import com.kroune.nineMensMorrisLib.Position
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    }

    /**
     * increases search depth
     */
    fun increaseDepth() {
        onDepthChange(
            depth() + 1
        )
    }

    /**
     * starts board analyze
     */
    fun startAnalyze(pos: Position): Flow<Position> {
        return flow {
            var currentPos = pos
            emit(currentPos)
            repeat(depth()) {
                currentCoroutineContext().ensureActive()
                val move = currentPos.findBestMove(depth().toUByte()) ?: return@flow
                currentPos = move.producePosition(currentPos)
                emit(currentPos)
            }
        }
    }
}
