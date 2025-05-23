package io.github.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kroune.nineMensMorrisLib.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext
import kotlin.math.max

/**
 * game analyze use case
 * uses local analysis
 */
class GameAnalyzeUseCase(
    val depth: StateFlow<Int>,
    val onDepthChange: (Int) -> Unit
) {
    /**
     * best moves as a list of move
     *
     * we don't need snapshotListState or smth like it, because we only update all list at once
     */
    val positionsValue: SnapshotStateList<Position> = SnapshotStateList()

    /**
     * decreases search depth
     */
    fun decreaseDepth() {
        onDepthChange(
            max(0, depth.value - 1)
        )
        stopAnalyze()
    }

    /**
     * increases search depth
     */
    fun increaseDepth() {
        onDepthChange(
            depth.value + 1
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
            repeat(depth.value) {
                coroutineContext.ensureActive()
                val move = currentPos.findBestMove(depth.value.toUByte()) ?: return@flow
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
