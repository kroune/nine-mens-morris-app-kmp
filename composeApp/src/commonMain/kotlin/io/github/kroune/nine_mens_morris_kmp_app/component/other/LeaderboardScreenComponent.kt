package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LeaderboardScreenComponent(
    private val onNavigationToAccountView: (Long) -> Unit,
    private val onNavigationBack: () -> Unit,
    private val accountInfoRepository: AccountInfoRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, KoinComponent {
    private val componentScope = componentCoroutineScope()

    private val leaderboardSize = 10
    private val _state = MutableStateFlow(
        LeaderboardScreenState(
            SnapshotStateList(leaderboardSize) {
                PlayerInfo(null, null, null, null)
            }
        )
    )
    val state: StateFlow<LeaderboardScreenState>
        get() = _state

    private var leaderboardData: LeaderboardApiResponses? = null
    private val useCases = mutableListOf<AccountInfoUseCase>()

    init {
        componentScope.launch {
            val localLeaderboardData = accountInfoRepository.getLeaderboard(leaderboardSize)
            leaderboardData = localLeaderboardData
            if (localLeaderboardData is LeaderboardApiResponses.Success) {
                val size = localLeaderboardData.leaderboard.size
                _state.update {
                    it.copy(
                        leaderboard = SnapshotStateList(size) {
                            PlayerInfo(null, null, null, null)
                        }
                    )
                }
                localLeaderboardData.leaderboard.forEachIndexed { index, id ->
                    useCases.add(
                        AccountInfoUseCase(
                            accountId = id,
                            onLoginResult = { result ->
                                _state.value.leaderboard[index] =
                                    _state.value.leaderboard[index].copy(
                                        loginResult = result
                                    )
                            },
                            onRatingResult = { result ->
                                _state.value.leaderboard[index] =
                                    _state.value.leaderboard[index].copy(
                                        ratingResult = result
                                    )
                            },
                            needPicture = { result ->
                                _state.value.leaderboard[index] =
                                    _state.value.leaderboard[index].copy(
                                        picture = result
                                    )
                            },
                            scope = componentScope,
                            accountInfoRepository = get()
                        )
                    )
                }
            }
        }
    }

    fun onEvent(event: LeaderboardEvent) {
        when (event) {
            is LeaderboardEvent.ReloadIcon -> {
                useCases[event.index].reloadPicture()
            }

            is LeaderboardEvent.ReloadName -> {
                useCases[event.index].reloadName()
            }

            is LeaderboardEvent.ReloadRating -> {
                useCases[event.index].reloadRating()
            }

            LeaderboardEvent.Back -> {
                onNavigationBack()
            }

            is LeaderboardEvent.NavigateToAccountView -> {
                val leaderboardDataState = leaderboardData
                if (leaderboardDataState is LeaderboardApiResponses.Success) {
                    val element = leaderboardDataState.leaderboard.getOrElse(event.index) {
                        return
                    }
                    onNavigationToAccountView(element)
                }
            }
        }
    }

    override fun onBackPressed() {
        onEvent(LeaderboardEvent.Back)
    }
}

data class PlayerInfo(
    val loginResult: LoginByIdApiResponses?,
    val ratingResult: RatingByIdApiResponses?,
    val creationDate: CreationDateByIdApiResponses?,
    val picture: AccountPictureByIdApiResponses?,
)

@Immutable
data class LeaderboardScreenState(
    val leaderboard: SnapshotStateList<PlayerInfo>
)