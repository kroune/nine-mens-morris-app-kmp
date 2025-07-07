package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LeaderboardScreenComponent(
    private val onNavigationToAccountView: (Long) -> Unit,
    private val onNavigationBack: () -> Unit,
    private val accountInfoRepository: Lazy<AccountInfoRepositoryI>,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
    private val componentScope = componentCoroutineScope()

    private val leaderboardSize = 10
    private val _state = MutableStateFlow(
        LeaderboardScreenState(
            List(leaderboardSize) {
                null
            }
        )
    )
    val state: StateFlow<LeaderboardScreenState>
        get() = _state

    private val useCases = mutableListOf<AccountInfoUseCase>()

    private fun startLoadingAccountsInfo(leaderboardData: List<Long>) {
        leaderboardData.forEachIndexed { index, accountId ->
            useCases.add(
                AccountInfoUseCase(
                    accountId = accountId,
                    onLoginResult = { result ->
                        _state.value.leaderboard[index]!!.loginResult.value = result
                    },
                    onRatingResult = { result ->
                        _state.value.leaderboard[index]!!.ratingResult.value = result
                    },
                    needPicture = { result ->
                        _state.value.leaderboard[index]!!.picture.value = result
                    },
                    scope = componentScope,
                    accountInfoRepository = get()
                )
            )
        }
    }

    init {
        componentScope.launch {
            val leaderboardDataState = accountInfoRepository.value.getLeaderboard(leaderboardSize)
            // success -> load other data
            if (leaderboardDataState is LeaderboardApiResponses.Success) {
                _state.update {
                    it.copy(
                        leaderboard = leaderboardDataState.leaderboard.map { playerId ->
                            LeaderBoardPlayerInfo(
                                playerId,
                                mutableStateOf(null),
                                mutableStateOf(null),
                                mutableStateOf(null),
                                mutableStateOf(null)
                            )
                        }
                    )
                }
                startLoadingAccountsInfo(leaderboardDataState.leaderboard)
            }
        }
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.ReloadIcon -> {
                useCases.getOrNull(event.index)?.reloadPicture()
            }

            is LeaderboardEvent.ReloadName -> {
                useCases.getOrNull(event.index)?.reloadName()
            }

            is LeaderboardEvent.ReloadRating -> {
                useCases.getOrNull(event.index)?.reloadRating()
            }

            LeaderboardEvent.Back -> {
                onNavigationBack()
            }

            is LeaderboardEvent.NavigateToAccountView -> {
                val accountId = _state.value.leaderboard
                    .getOrNull(event.index)?.accountId ?: return
                onNavigationToAccountView(accountId)
            }
        }
    }

    override fun onBackPressed() {
        onEvent(LeaderboardEvent.Back)
    }
}

@Immutable
data class LeaderBoardPlayerInfo(
    val accountId: Long,
    val loginResult: MutableState<LoginByIdApiResponses?>,
    val ratingResult: MutableState<RatingByIdApiResponses?>,
    val creationDate: MutableState<CreationDateByIdApiResponses?>,
    val picture: MutableState<AccountPictureByIdApiResponses<ImageBitmap>?>,
)

@Immutable
data class LeaderboardScreenState(
    val leaderboard: List<LeaderBoardPlayerInfo?>
)