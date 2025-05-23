package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardScreenComponent(
    private val onNavigationToAccountView: (Long) -> Unit,
    private val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
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
        CoroutineScope(Dispatchers.Default).launch {
            val localLeaderboardData = accountInfoInteractor.getLeaderboard(leaderboardSize)
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
                    AccountInfoUseCase(
                        accountId = id,
                        onLoginResult = { result ->
                            _state.value.leaderboard[index] = _state.value.leaderboard[index].copy(
                                loginResult = result
                            )
                        },
                        onRatingResult = { result ->
                            _state.value.leaderboard[index] = _state.value.leaderboard[index].copy(
                                ratingResult = result
                            )
                        },
                        needPicture = { result ->
                            _state.value.leaderboard[index] = _state.value.leaderboard[index].copy(
                                picture = result
                            )
                        },
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
                if (leaderboardDataState is LeaderboardApiResponses.Success)
                    onNavigationToAccountView(leaderboardDataState.leaderboard[event.index])
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

data class LeaderboardScreenState(
    val leaderboard: SnapshotStateList<PlayerInfo>
)