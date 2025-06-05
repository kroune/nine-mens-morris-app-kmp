package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.onlineGameInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import io.github.kroune.nine_mens_morris_kmp_app.useCases.GameBoardUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class OnlineGameComponent(
    private val onNavigationToViewAccountScreen: (Long) -> Unit,
    private val onNavigationToViewOwnAccountScreen: (Long) -> Unit,
    private val gameId: Long,
    private val onNavigationToWelcomeScreen: () -> Unit,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val _state = MutableStateFlow(
        OnlineGameScreenState(
            position = Position(
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
            ),
            selectedButton = null,
            isGreen = false,
            timeLeft = 30,
            moveHints = setOf(),
            gameEnded = false,

            displayGiveUpConfirmation = false,
            ownAccountLoginResult = null,
            ownAccountRatingResult = null,
            ownAccountPictureResult = null,
            enemyAccountLoginResult = null,
            enemyAccountRatingResult = null,
            enemyAccountPictureResult = null,
        )
    )
    val state
        get() = _state

    private val componentScope = componentCoroutineScope()

    private var enemyAccountId: Long? = null
    private var ownAccountId: Long? = null

    private val gameUseCase = GameBoardUseCase(
        pos = {
            _state.value.position
        },
        onPositionChange = { value ->
            _state.update {
                it.copy(
                    position = value
                )
            }
        },
        onGameEnd = {
            _state.update {
                it.copy(
                    gameEnded = true
                )
            }
        },
        selectedButton = {
            _state.value.selectedButton
        },
        onSelectedButtonUpdate = { value ->
            _state.update {
                it.copy(
                    selectedButton = value
                )
            }
        },
        onMoveHintsUpdate = { value ->
            _state.update {
                it.copy(
                    moveHints = value
                )
            }
        }
    )
    private val channelToSendMoves: Channel<Movement> = Channel()
    private val channelToReceiveMoves: Channel<Movement> = Channel()
    val onGiveUp: suspend () -> Unit = {
        _state.update {
            it.copy(
                gameEnded = true
            )
        }
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
                    .let { value ->
                        state.update {
                            it.copy(
                                position = value.first.startPosition.await(),
                                isGreen = value.first.isGreen.await()
                            )
                        }
                        enemyId = value.first.enemyId.await()
                        onGiveClose = value.second
                        gameEnded = value.first.gameEnded
                    }
                val accountIdResult = accountIdInteractor.getAccountId()
                if (accountIdResult !is AccountIdByJwtTokenApiResponses.Success) {
                    error("accountId is not success")
                }
                ownAccountId = accountIdResult.accountId
                ownAccountInfoUseCase = AccountInfoUseCase(
                    accountId = accountIdResult.accountId,
                    onLoginResult = { result ->
                        _state.update {
                            it.copy(
                                ownAccountLoginResult = result
                            )
                        }
                    },
                    onRatingResult = { result ->
                        _state.update {
                            it.copy(
                                ownAccountRatingResult = result
                            )
                        }
                    },
                    needPicture = { result ->
                        _state.update {
                            it.copy(
                                ownAccountPictureResult = result
                            )
                        }
                    }
                )
                enemyAccountId = enemyId
                enemyAccountInfoUseCase = AccountInfoUseCase(
                    accountId = enemyId,
                    onLoginResult = { result ->
                        _state.update {
                            it.copy(
                                enemyAccountLoginResult = result
                            )
                        }
                    },
                    onRatingResult = { result ->
                        _state.update {
                            it.copy(
                                enemyAccountRatingResult = result
                            )
                        }
                    },
                    needPicture = { result ->
                        _state.update {
                            it.copy(
                                enemyAccountPictureResult = result
                            )
                        }
                    }
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
                _state.update {
                    it.copy(
                        gameEnded = true
                    )
                }
            }.onFailure {
                println("caught unhandled exception at online game ${it.stackTraceToString()}")
                withContext(Dispatchers.Main) {
                    onNavigationToWelcomeScreen()
                }
            }
        }
        componentScope.launch {
            while (!_state.value.gameEnded) {
                _state.update {
                    it.copy(
                        timeLeft = (it.timeLeft - 1).coerceAtLeast(0)
                    )
                }
                delay(1.seconds)
            }
        }
    }

    fun onEvent(event: OnlineGameScreenEvent) {
        when (event) {
            OnlineGameScreenEvent.GiveUp -> {
                displayGiveUpConfirmation.value = false
                componentScope.launch {
                    onGiveUp()
                }
            }

            is OnlineGameScreenEvent.Click -> {
                if (_state.value.gameEnded) {
                    return
                }
                if (_state.value.isGreen == _state.value.position.pieceToMove) {
                    val move = gameUseCase.handleClick(event.index)
                    if (move != null) {
                        gameUseCase.processMove(move)
                        _state.update {
                            it.copy(
                                moveHints = setOf()
                            )
                        }
                        // post our move
                        componentScope.launch {
                            channelToSendMoves.trySend(move).onFailure {
                                // game has ended || some exception occurred
                                return@launch
                            }
                            _state.update {
                                it.copy(
                                    timeLeft = 30
                                )
                            }
                        }
                    } else {
                        gameUseCase.handleHighLighting()
                    }
                } else {
                    // we can't make any move if it isn't our move
                    _state.update {
                        it.copy(
                            moveHints = setOf()
                        )
                    }
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
        if (!_state.value.gameEnded)
            displayGiveUpConfirmation.value = true
        else
            onEvent(OnlineGameScreenEvent.NavigateToMainScreen)
    }
}

data class OnlineGameScreenState(
    val position: Position,
    val selectedButton: Int?,
    val isGreen: Boolean,
    val timeLeft: Int,
    val moveHints: Set<Int>,
    val gameEnded: Boolean,
    val displayGiveUpConfirmation: Boolean,

    val ownAccountLoginResult: LoginByIdApiResponses?,
    val ownAccountRatingResult: RatingByIdApiResponses?,
    val ownAccountPictureResult: AccountPictureByIdApiResponses?,

    val enemyAccountLoginResult: LoginByIdApiResponses?,
    val enemyAccountRatingResult: RatingByIdApiResponses?,
    val enemyAccountPictureResult: AccountPictureByIdApiResponses?
)