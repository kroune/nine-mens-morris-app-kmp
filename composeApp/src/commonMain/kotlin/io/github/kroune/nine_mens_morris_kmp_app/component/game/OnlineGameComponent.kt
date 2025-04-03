package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.onlineGameInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class OnlineGameComponent(
    private val onNavigationToViewAccountScreen: (Long) -> Unit,
    private val onNavigationToViewOwnAccountScreen: (Long) -> Unit,
    private val gameId: Long,
    private val onNavigationToWelcomeScreen: () -> Unit,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    private val _enemyAccountId = mutableStateOf<Long?>(null)
    val enemyAccountId by _enemyAccountId

    private var _enemyAccountName = mutableStateOf<LoginByIdApiResponses?>(null)
    val enemyAccountName by _enemyAccountName

    private var _enemyPictureByteArray = mutableStateOf<AccountPictureByIdApiResponses?>(null)
    val enemyPictureByteArray by _enemyPictureByteArray

    private var _enemyAccountRating = mutableStateOf<RatingByIdApiResponses?>(null)
    val enemyAccountRating by _enemyAccountRating

    private val _ownAccountId = mutableStateOf<Long?>(null)
    val ownAccountId by _ownAccountId

    private var _ownAccountName = mutableStateOf<LoginByIdApiResponses?>(null)
    val ownAccountName by _ownAccountName

    private var _ownPictureByteArray = mutableStateOf<AccountPictureByIdApiResponses?>(null)
    val ownPictureByteArray by _ownPictureByteArray

    private var _ownAccountRating = mutableStateOf<RatingByIdApiResponses?>(null)
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
    private val channelToReceiveMoves: Channel<Movement> = Channel()
    val onGiveUp: suspend () -> Unit = {
        _gameEnded.value = true
        channelToSendMoves.trySend(Movement(null, null))
    }
    private var onGiveClose: (suspend () -> Unit)? = null
    var displayGiveUpConfirmation = mutableStateOf(false)
    private lateinit var ownAccountInfoUseCase: AccountInfoUseCase
    private lateinit var enemyAccountInfoUseCase: AccountInfoUseCase


    init {
        componentScope.launch {
            runCatching {
                val gameEnded: CompletableDeferred<Boolean>
                // TODO: handle errors
                val enemyId: Long
                onlineGameInteractor.connect(gameId, channelToSendMoves, channelToReceiveMoves)
                    .let {
                        _position.value = it.first.startPosition.await()
                        _isGreen.value = it.first.isGreen.await()
                        enemyId = it.first.enemyId.await()
                        onGiveClose = it.second
                        gameEnded = it.first.gameEnded
                    }
                val accountIdResult = accountIdInteractor.getAccountId()
                if (accountIdResult !is AccountIdByJwtTokenApiResponses.Success) {
                    error("accountId is not success")
                }
                _ownAccountId.value = accountIdResult.accountId
                ownAccountInfoUseCase = AccountInfoUseCase(
                    accountIdResult.accountId,
                    needCreationDate = false,
                    playerInfo = AccountInfoUseCase.PlayerInfo(
                        name = _ownAccountName,
                        rating = _ownAccountRating,
                        accountPicture = _ownPictureByteArray
                    )
                )
                _enemyAccountId.value = enemyId
                enemyAccountInfoUseCase = AccountInfoUseCase(
                    enemyId,
                    needCreationDate = false,
                    playerInfo = AccountInfoUseCase.PlayerInfo(
                        name = _enemyAccountName,
                        rating = _enemyAccountRating,
                        accountPicture = _enemyPictureByteArray
                    )
                )
                while (!gameEnded.isCompleted) {
                    val moveResult = channelToReceiveMoves.receiveCatching()
                    if (moveResult.isFailure) {
                        // game ended
                        if (gameEnded.isCompleted && gameEnded.getCompleted()) {
                            break
                        } else {
                            // some error happened
                            // TODO: notify user
                            println(moveResult.exceptionOrNull()!!.stackTraceToString())
                            withContext(Dispatchers.Main) {
                                onNavigationToWelcomeScreen()
                            }
                            return@launch
                        }
                    }
                    val move = moveResult.getOrThrow()
                    gameUseCase.processMove(move)
                }
                _gameEnded.value = true
            }.onFailure {
                println("caught unhandled exception at online game ${it.stackTraceToString()}")
                withContext(Dispatchers.Main) {
                    onNavigationToWelcomeScreen()
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            while (!gameEnded) {
                timeLeft = max(timeLeft - 1, 0)
                delay(1.seconds)
            }
        }
    }

    fun onEvent(event: OnlineGameScreenEvent) {
        when (event) {
            OnlineGameScreenEvent.GiveUp -> {
                displayGiveUpConfirmation.value = false
                CoroutineScope(Dispatchers.Default).launch {
                    onGiveUp()
                }
            }

            is OnlineGameScreenEvent.Click -> {
                if (_gameEnded.value) {
                    return
                }
                if (isGreen == gameUseCase.pos.value.pieceToMove) {
                    val move = gameUseCase.handleClick(event.index)
                    if (move != null) {
                        gameUseCase.processMove(move)
                        gameUseCase.moveHints.value = listOf()
                        // post our move
                        CoroutineScope(Dispatchers.Default).launch {
                            channelToSendMoves.trySend(move).onFailure {
                                // game has ended || some exception occurred
                                return@launch
                            }
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

            OnlineGameScreenEvent.NavigateToMainScreen -> {
                onNavigationToWelcomeScreen()
            }

            OnlineGameScreenEvent.GiveUpDiscarded -> {
                displayGiveUpConfirmation.value = false
            }

            is OnlineGameScreenEvent.ReloadIcon -> {
                if (event.ownAccount) {
                    ownAccountInfoUseCase.reloadPicture()
                } else {
                    enemyAccountInfoUseCase.reloadPicture()
                }
            }

            is OnlineGameScreenEvent.ReloadName -> {
                if (event.ownAccount) {
                    ownAccountInfoUseCase.reloadName()
                } else {
                    enemyAccountInfoUseCase.reloadName()
                }
            }

            is OnlineGameScreenEvent.ReloadRating -> {
                if (event.ownAccount) {
                    ownAccountInfoUseCase.reloadRating()
                } else {
                    enemyAccountInfoUseCase.reloadRating()
                }
            }

            OnlineGameScreenEvent.NavigateToAccountView -> {
                enemyAccountId?.let {
                    onNavigationToViewAccountScreen(it)
                }
            }

            OnlineGameScreenEvent.NavigateToOwnAccountView -> {
                ownAccountId?.let {
                    onNavigationToViewOwnAccountScreen(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!gameEnded)
            displayGiveUpConfirmation.value = true
        else
            onEvent(OnlineGameScreenEvent.NavigateToMainScreen)
    }
}