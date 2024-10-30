package com.kroune.nine_mens_morris_kmp_app.useCases

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kroune.nineMensMorrisLib.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * game analyze use case
 * uses local analysis
 */
class GameAnalyzeUseCase() {
    /**
     * depth at which search will be performed
     */
    var depthValue = mutableIntStateOf(4)

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
        depthValue.value = max(0, depthValue.value - 1)
        stopAnalyze()
    }

    /**
     * increases search depth
     */
    fun increaseDepth() {
        depthValue.value++
        stopAnalyze()
    }

    /**
     * current analyze job
     */
    var analyzeJob: Job? = null

    /**
     * starts board analyze
     */
    fun startAnalyze(pos: Position) {
        analyzeJob = CoroutineScope(Dispatchers.Default).launch {
            var currentPos = pos
            // see https://github.com/detekt/detekt/issues/3566
            // however we can't exit repeat with break
            @Suppress("UnusedPrivateProperty")
            for (i in 1..depthValue.value) {
                val move = currentPos.findBestMove(depthValue.value.toUByte()) ?: break
                positionsValue.add(currentPos)
                currentPos = move.producePosition(currentPos)
            }
            positionsValue.add(currentPos)
        }
        analyzeJob?.start()
    }

    /**
     * hides analyze gui and delete it's result
     */
    private fun stopAnalyze() {
        analyzeJob?.cancel()
    }
}
