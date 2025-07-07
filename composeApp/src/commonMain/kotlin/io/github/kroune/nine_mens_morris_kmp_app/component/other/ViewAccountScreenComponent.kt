package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.ui.graphics.ImageBitmap
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.PastGamesUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.mappers.toUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game.GameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.concurrent.Volatile
import kotlin.time.ExperimentalTime

class ViewAccountScreenComponent(
    private val onNavigationBack: () -> Unit,
    private val accountId: Long,
    private val jwtTokenInteractor: JwtTokenRepositoryI,
    private val gameRepository: GameRepositoryI,
    private val accountInfoRepository: AccountInfoRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
    private val componentScope = componentCoroutineScope()

    private val _state = MutableStateFlow(
        ViewAccountScreenState(
            accountId,
            null,
            null,
            null,
            null,
            1,
            null
        )
    )
    val state: StateFlow<ViewAccountScreenState>
        get() = _state

    @Volatile
    var getPastGamesJob: Job? = null
    val pastGamesJobLock = Mutex()

    init {
        updatePastGamesList()
    }

    @OptIn(ExperimentalTime::class)
    fun updatePastGamesList() {
        componentScope.launch {
            pastGamesJobLock.withLock {
                getPastGamesJob?.cancel()
                getPastGamesJob = launch {
                    val pastGames = gameRepository.getPastGames(
                        accountId,
                        10,
                        (_state.value.playedGamesListPage - 1) * 10L
                    )
                    val pastGamesUiModel: PastGamesApiResponse<List<PastGamesUiModel>>? =
                        when (pastGames) {
                            is PastGamesApiResponse.Success<List<PastGamesHistoryItem>> -> {
                                val pastGamesUiModelList = pastGames.playedGames.map {
                                    it.toUiModel(
                                        accountInfoRepository,
                                        componentScope
                                    )
                                }
                                PastGamesApiResponse.Success<List<PastGamesUiModel>>(
                                    pastGamesUiModelList
                                )
                            }

                            else -> {
                                pastGames as PastGamesApiResponse<List<PastGamesUiModel>>?
                            }
                        }
                    _state.update {
                        it.copy(
                            playedGamesList = pastGamesUiModel
                        )
                    }
                }
            }
        }
    }

    private val accountInfoUseCase = AccountInfoUseCase(
        accountId = accountId,
        onLoginResult = { result ->
            _state.update {
                it.copy(
                    accountLoginResult = result
                )
            }
        },
        onRatingResult = { result ->
            _state.update {
                it.copy(
                    accountRatingResult = result
                )
            }
        },
        needCreationDate = { result ->
            _state.update {
                it.copy(
                    accountCreationDateResult = result
                )
            }
        },
        needPicture = { result ->
            _state.update {
                it.copy(
                    accountPictureResult = result
                )
            }
        },
        scope = componentScope,
        get()
    )

    fun onEvent(event: ViewAccountScreenEvent) {
        when (event) {
            ViewAccountScreenEvent.Logout -> {
                jwtTokenInteractor.logout()
                onNavigationBack()
            }

            ViewAccountScreenEvent.ReloadCreationDate -> {
                accountInfoUseCase.reloadCreationDate()
            }

            ViewAccountScreenEvent.ReloadIcon -> {
                accountInfoUseCase.reloadPicture()
            }

            ViewAccountScreenEvent.ReloadName -> {
                accountInfoUseCase.reloadName()
            }

            ViewAccountScreenEvent.ReloadRating -> {
                accountInfoUseCase.reloadRating()
            }

            ViewAccountScreenEvent.Back -> {
                onNavigationBack()
            }

            ViewAccountScreenEvent.NextPage -> {
                _state.update {
                    it.copy(
                        playedGamesListPage = _state.value.playedGamesListPage + 1
                    )
                }
                updatePastGamesList()
            }

            ViewAccountScreenEvent.PreviousPage -> {
                val newPlayedGamesListPage = _state.value.playedGamesListPage - 1
                if (newPlayedGamesListPage < 1)
                    return
                _state.update {
                    it.copy(
                        playedGamesListPage = newPlayedGamesListPage
                    )
                }
                updatePastGamesList()
            }

            is ViewAccountScreenEvent.NavigateToViewPastGame -> TODO()
        }
    }

    override fun onBackPressed() {
        onEvent(ViewAccountScreenEvent.Back)
    }
}

data class ViewAccountScreenState(
    val accountId: Long,
    val accountLoginResult: LoginByIdApiResponses?,
    val accountRatingResult: RatingByIdApiResponses?,
    val accountCreationDateResult: CreationDateByIdApiResponses?,
    val accountPictureResult: AccountPictureByIdApiResponses<ImageBitmap>?,
    val playedGamesListPage: Int,
    val playedGamesList: PastGamesApiResponse<List<PastGamesUiModel>>?,
)

@Serializable
data class PastGamesHistoryItem(
    val gameId: Long,
    val firstPlayerId: Long,
    val secondPlayerId: Long,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val totalMoves: Int,
    val firstPlayerMovesFirst: Boolean,
    val gameEndResult: String,
    val firstPlayerRatingDelta: Int,
    val secondPlayerRatingDelta: Int,
)
