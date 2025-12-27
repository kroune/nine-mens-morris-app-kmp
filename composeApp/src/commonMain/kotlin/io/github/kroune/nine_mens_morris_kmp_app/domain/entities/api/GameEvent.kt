package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement

sealed interface GameEvent {

    sealed interface Success : GameEvent {
        class GameInfo(
            val isGreen: Boolean,
            val startPosition: Position,
            val enemyId: Long,
        ) : Success

        object GameEnded : Success

        class Move(val movement: Movement) : Success
    }

    sealed interface Error : GameEvent {
        object NetworkError : Error
        object ServerError : Error
        object UnknownError : Error
    }
}
