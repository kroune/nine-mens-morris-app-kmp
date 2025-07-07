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
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.mappers.toUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.game.GameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.concurrent.Volatile
import kotlin.time.ExperimentalTime

class ViewOwnAccountScreenComponent(
    private val onNavigationBack: () -> Unit,
    private val accountId: Long,
    private val accountInfoRepository: AccountInfoRepositoryI,
    private val jwtTokenInteractor: JwtTokenRepositoryI,
    private val gameRepository: GameRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
    private val componentScope = componentCoroutineScope()

    private val _state = MutableStateFlow(
        ViewOwnAccountScreenState(
            null,
            null,
            null,
            null,
            null,
            1,
            null,
            false,
        )
    )
    val state: StateFlow<ViewOwnAccountScreenState>
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
                    delay(5000)
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

    fun onEvent(event: ViewOwnAccountScreenEvent) {
        when (event) {
            ViewOwnAccountScreenEvent.OnLogoutPressed -> {
                jwtTokenInteractor.logout()
                onNavigationBack()
            }

            ViewOwnAccountScreenEvent.ReloadCreationDate -> {
                accountInfoUseCase.reloadCreationDate()
            }

            ViewOwnAccountScreenEvent.ReloadIcon -> {
                accountInfoUseCase.reloadPicture()
            }

            ViewOwnAccountScreenEvent.ReloadName -> {
                accountInfoUseCase.reloadName()
            }

            ViewOwnAccountScreenEvent.ReloadRating -> {
                accountInfoUseCase.reloadRating()
            }

            ViewOwnAccountScreenEvent.OnBackPressed -> {
                onNavigationBack()
            }

            is ViewOwnAccountScreenEvent.UploadNewPicture -> {
                _state.update {
                    it.copy(
                        isUploadingNewPictureInProgress = true
                    )
                }
                componentScope.launch {
                    _state.update {
                        it.copy(
                            uploadingNewPictureResult = accountInfoRepository.uploadPicture(event.picture),
                            isUploadingNewPictureInProgress = false
                        )
                    }
                }
            }

            ViewOwnAccountScreenEvent.NextPage -> {
                _state.update {
                    it.copy(
                        playedGamesListPage = _state.value.playedGamesListPage + 1
                    )
                }
                updatePastGamesList()
            }

            ViewOwnAccountScreenEvent.PreviousPage -> {
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

            is ViewOwnAccountScreenEvent.NavigateToViewPastGame -> TODO()
        }
    }

    override fun onBackPressed() {
        onEvent(ViewOwnAccountScreenEvent.OnBackPressed)
    }
}

data class ViewOwnAccountScreenState(
    val uploadingNewPictureResult: UploadPictureApiResponses?,
    val accountLoginResult: LoginByIdApiResponses?,
    val accountRatingResult: RatingByIdApiResponses?,
    val accountCreationDateResult: CreationDateByIdApiResponses?,
    val accountPictureResult: AccountPictureByIdApiResponses<ImageBitmap>?,
    val playedGamesListPage: Int,
    val playedGamesList: PastGamesApiResponse<List<PastGamesUiModel>>?,
    val isUploadingNewPictureInProgress: Boolean,
)