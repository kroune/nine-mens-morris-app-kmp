package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGameInteractor
import com.kroune.nine_mens_morris_kmp_app.event.OnlineGameScreenEvent
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import com.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds

class OnlineGameScreenComponent(
    val gameId: Long,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private var _enemyAccountName = mutableStateOf<Result<String>?>(null)
    var enemyAccountName by _enemyAccountName

    private var _enemyPictureByteArray = mutableStateOf<Result<ByteArray>?>(null)
    val enemyPictureByteArray by _enemyPictureByteArray

    private var _enemyAccountRating = mutableStateOf<Result<Long>?>(null)
    var enemyAccountRating by _enemyAccountRating

    private var _ownAccountName = mutableStateOf<Result<String>?>(null)
    val ownAccountName by _ownAccountName

    private var _ownPictureByteArray = mutableStateOf<Result<ByteArray>?>(null)
    val ownPictureByteArray by _ownPictureByteArray

    private var _ownAccountRating = mutableStateOf<Result<Long>?>(null)
    val ownAccountRating by _ownAccountRating
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
    private val gameUseCase = GameBoardUseCase(_position, onGameEnd = {})
    val selectedButton by gameUseCase.selectedButton
    val moveHints by gameUseCase.moveHints
    var timeLeft by mutableStateOf(30)
    private val channelToSendMoves: Channel<Movement> = Channel()


    init {
        CoroutineScope(Dispatchers.Default).launch {
            // TODO: handle errors
            onlineGameInteractor.connect(gameId, channelToSendMoves)
            _position.value = onlineGameInteractor.positionReceivedOnConnection!!
            gameUseCase.handleHighLighting()
            _gameEnded = onlineGameInteractor.gameEnded
            _isGreen.value = onlineGameInteractor.receivedIsGreenStatus!!
            AccountInfoUseCase(
                accountIdInteractor.getAccountId().getOrThrow(),
                needCreationDate = false,
                name = _ownAccountName,
                rating = _ownAccountRating,
                accountPicture = _ownPictureByteArray
            )
            AccountInfoUseCase(
                onlineGameInteractor.enemyId!!,
                needCreationDate = false,
                name = _enemyAccountName,
                rating = _enemyAccountRating,
                accountPicture = _enemyPictureByteArray
            )
            onlineGameInteractor.receivedMovesChannel.receiveAsFlow().onEach {
                gameUseCase.processMove(it)
            }.collect()
        }
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                timeLeft = max(timeLeft - 1, 0)
                delay(1.seconds)
            }
        }
    }

    fun onEvent(event: OnlineGameScreenEvent) {
        when (event) {
            OnlineGameScreenEvent.GiveUp -> {
                CoroutineScope(Dispatchers.Default).launch {
                    onlineGameInteractor.giveUp()
                }
            }

            is OnlineGameScreenEvent.Click -> {
                if (isGreen == gameUseCase.pos.value.pieceToMove) {
                    val move = gameUseCase.handleClick(event.index)
                    if (move != null) {
                        gameUseCase.processMove(move)
                        gameUseCase.moveHints.value = listOf()
                        // post our move
                        CoroutineScope(Dispatchers.Default).launch {
                            channelToSendMoves.send(move)
                            timeLeft = 30
                        }
                    } else {
                        gameUseCase.handleHighLighting()
                    }
                } else {
                    // we can't make any move if it isn't our move
                    gameUseCase.moveHints.value = listOf()
                }
            }
        }
    }
}