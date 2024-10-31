package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGameInteractor
import com.kroune.nine_mens_morris_kmp_app.event.OnlineGameScreenEvent
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import com.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnlineGameScreenComponent(
    val gameId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var enemyAccountName by mutableStateOf<Result<String>?>(null)
    var enemyPictureByteArray by mutableStateOf<Result<ByteArray>?>(null)
    var enemyAccountRating by mutableStateOf<Result<Long>?>(null)
    var ownAccountName by mutableStateOf<Result<String>?>(null)
    var ownPictureByteArray by mutableStateOf<Result<ByteArray>?>(null)
    var ownAccountRating by mutableStateOf<Result<Long>?>(null)
    private val _position = mutableStateOf(
        Position(
            // @formatter:off
            arrayOf(
                EMPTY,                  EMPTY,                  EMPTY,
                        EMPTY,          EMPTY,          EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                EMPTY,  EMPTY,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                                EMPTY,  EMPTY,  EMPTY,
                        EMPTY,          EMPTY,          EMPTY,
                EMPTY,                  EMPTY,                  EMPTY
            ),
            // @formatter:on
            0u, 0u, pieceToMove = true
        )
    )
    val position by _position
    private var _gameEnded = mutableStateOf(false)
    val gameEnded by _gameEnded
    private val _isGreen = mutableStateOf(false)
    val isGreen by _isGreen
    val gameUseCase = GameBoardUseCase(_position, onGameEnd = {})
    val selectedButton by gameUseCase.selectedButton
    val moveHints by gameUseCase.moveHints
    val onClick = { index: Int ->
        with(gameUseCase) {
            gameUseCase.onClick(index)
        }
    }
    val timeLeft by mutableStateOf(30)


    init {
        CoroutineScope(Dispatchers.Default).launch {
            // TODO: handle errors
            onlineGameInteractor.connect(gameId)
            onlineGameInteractor.gettingBasicInfoJob.join()
            _position.value = onlineGameInteractor.positionReceivedOnConnection!!
            gameUseCase.handleHighLighting()
            _gameEnded = onlineGameInteractor.gameEnded
            _isGreen.value = onlineGameInteractor.receivedIsGreenStatus!!
            val ownInfo = AccountInfoUseCase(accountIdInteractor.getAccountId().getOrThrow())
            val enemyInfo = AccountInfoUseCase(onlineGameInteractor.enemyId!!)
            enemyAccountName = enemyInfo.name
            enemyPictureByteArray = enemyInfo.accountPicture
            enemyAccountRating = enemyInfo.rating
            ownAccountName = ownInfo.name
            ownPictureByteArray = ownInfo.accountPicture
            ownAccountRating = ownInfo.rating
        }
    }

    fun onEvent(event: OnlineGameScreenEvent) {
        when (event) {
            OnlineGameScreenEvent.GiveUp -> {
                CoroutineScope(Dispatchers.Default).launch {
                    onlineGameInteractor.giveUp()
                }
            }
        }
    }
}