package io.github.kroune.nine_mens_morris_kmp_app.component.game

import com.arkivanov.decompose.ComponentContext
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.common.map
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.GameEvent
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            moveHints = listOf(),
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
        pos = _state.map(componentScope) { it.position },
        onPositionChange = { value ->
            _state.update {
                it.copy(
                    position = value
                )
            }
        },
        onGameEnd = {},
        selectedButton = _state.map(componentScope) { it.selectedButton },
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
    private lateinit var ownAccountInfoUseCase: AccountInfoUseCase
    private lateinit var enemyAccountInfoUseCase: AccountInfoUseCase

    init {
        componentScope.launch {
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
        }
        componentScope.launch {
            // TODO: handle errors
            onlineGameInteractor.connect(gameId).collect {
                println(it)
                when (it) {
                    is GameEvent.EnemyIdEvent -> {
                        val enemyId = it.enemyId
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
                    }

                    is GameEvent.GameEnd -> {
                        _state.update {
                            it.copy(
                                gameEnded = true
                            )
                        }
                    }

                    is GameEvent.IsGreenEvent -> {
                        val isGreen = it.isGreen
                        _state.update {
                            it.copy(
                                isGreen = isGreen
                            )
                        }
                    }

                    is GameEvent.MovementEvent -> {
                        val move = it.data
                        gameUseCase.processMove(move)
                    }

                    is GameEvent.PositionEvent -> {
                        val position = it.position
                        _state.update {
                            it.copy(
                                position = position
                            )
                        }
                    }
                }
            }
            println("GAME ENDED")
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
                _state.update {
                    it.copy(
                        displayGiveUpConfirmation = false
                    )
                }
                componentScope.launch {
                    val result = onlineGameInteractor.giveUp(gameId)
                    println(result)
                }
            }

            is OnlineGameScreenEvent.Click -> {
                if (_state.value.gameEnded) {
                    return
                }
                if (_state.value.isGreen == gameUseCase.pos.value.pieceToMove) {
                    val move = gameUseCase.handleClick(event.index)
                    if (move != null) {
                        gameUseCase.processMove(move)
                        _state.update {
                            it.copy(
                                moveHints = listOf()
                            )
                        }
                        // post our move
                        componentScope.launch {
                            // TODO: handle errors
                            val result = onlineGameInteractor.sendMove(move, gameId)
                            println(result)
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
                            moveHints = listOf()
                        )
                    }
                }
            }

            OnlineGameScreenEvent.NavigateToMainScreen -> {
                onNavigationToWelcomeScreen()
            }

            OnlineGameScreenEvent.GiveUpDiscarded -> {
                _state.update {
                    it.copy(
                        displayGiveUpConfirmation = false
                    )
                }
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
        if (!_state.value.gameEnded) {
            _state.update {
                it.copy(
                    displayGiveUpConfirmation = true
                )
            }
        } else
            onEvent(OnlineGameScreenEvent.NavigateToMainScreen)
    }
}

data class OnlineGameScreenState(
    val position: Position,
    val selectedButton: Int?,
    val isGreen: Boolean,
    val timeLeft: Int,
    val moveHints: List<Int>,
    val gameEnded: Boolean,
    val displayGiveUpConfirmation: Boolean,

    val ownAccountLoginResult: LoginByIdApiResponses?,
    val ownAccountRatingResult: RatingByIdApiResponses?,
    val ownAccountPictureResult: AccountPictureByIdApiResponses?,

    val enemyAccountLoginResult: LoginByIdApiResponses?,
    val enemyAccountRatingResult: RatingByIdApiResponses?,
    val enemyAccountPictureResult: AccountPictureByIdApiResponses?
)