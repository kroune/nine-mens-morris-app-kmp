package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

import androidx.compose.runtime.MutableState
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.channels.Channel

interface OnlineGameInteractorI {
    val receivedIsGreenStatus: Boolean?
    val receivedMovesChannel: Channel<Movement>
    val movesToSendChannel: Channel<Movement>
    val gameEnded : MutableState<Boolean>
    val positionReceivedOnConnection: Position?
    val enemyId: Long?
    var gettingBasicInfoJob: CompletableJob
    suspend fun connect(gameId: Long)
    suspend fun giveUp()
}