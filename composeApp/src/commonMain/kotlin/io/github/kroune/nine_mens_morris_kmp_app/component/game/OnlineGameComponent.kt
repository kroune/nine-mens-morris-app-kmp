package io.github.kroune.nine_mens_morris_kmp_app.component.game

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nineMensMorrisLib.move.Movement
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.GameEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.OnlineGameScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountId.AccountIdRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.onlineGame.OnlineGameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.GameBoardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class OnlineGameComponent(
    private val onNavigationToViewAccountScreen: (Long) -> Unit,
    private val onNavigationToViewOwnAccountScreen: (Long) -> Unit,
    private val gameId: Long,
    private val onNavigationToWelcomeScreen: () -> Unit,
    private val onlineGameRepository: OnlineGameRepositoryI,
    private val accountIdRepository: AccountIdRepositoryI,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
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
        getPosition = {
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
        getSelectedButton = { _state.value.selectedButton },
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

    private val channelToSendMoves: MutableSharedFlow<Movement> = MutableSharedFlow()
    private val onGiveUp: suspend () -> Unit = {
        _state.update {
            it.copy(
                gameEnded = true
            )
        }
        channelToSendMoves.emit(Movement(null, null))
    }
    var displayGiveUpConfirmation = mutableStateOf(false)
    private lateinit var ownAccountInfoUseCase: AccountInfoUseCase
    private lateinit var enemyAccountInfoUseCase: AccountInfoUseCase


    init {
        componentScope.launch {
            val accountIdResult = accountIdRepository.getAccountId()
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
                },
                scope = componentScope,
                accountInfoRepository = get()
            )
        }
        componentScope.launch {
            onlineGameRepository.connect(gameId, channelToSendMoves).collect { event ->
                if (event is GameEvent.Error) {
                    println(event)
                    withContext(Dispatchers.Main) {
                        onNavigationToWelcomeScreen()
                    }
                } else {
                    when (event) {
                        GameEvent.Success.GameEnded -> {
                            _state.update {
                                it.copy(gameEnded = true)
                            }
                        }

                        is GameEvent.Success.GameInfo -> {
                            state.update {
                                it.copy(
                                    position = event.startPosition,
                                    isGreen = event.isGreen,
                                )
                            }
                            val enemyId = event.enemyId
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
                                },
                                scope = componentScope,
                                accountInfoRepository = get()
                            )
                        }

                        is GameEvent.Success.Move -> {
                            gameUseCase.processMovement(event.movement)
                        }
                    }
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
                        gameUseCase.processMovement(move)
                        _state.update {
                            it.copy(
                                moveHints = setOf()
                            )
                        }
                        // post our move
                        componentScope.launch {
                            channelToSendMoves.emit(move)
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
