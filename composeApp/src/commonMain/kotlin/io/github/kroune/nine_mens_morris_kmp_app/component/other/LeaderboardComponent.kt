package io.github.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.mutableStateListOf
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.other.LeaderboardEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardComponent(
    private val onNavigationToAccountView: (Long) -> Unit,
    private val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private var leaderboardData: LeaderboardApiResponses? = null
    val players = mutableStateListOf<AccountInfoUseCase.PlayerInfo>()
    private val useCases = mutableListOf<AccountInfoUseCase>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val localLeaderboardData = accountInfoInteractor.getLeaderboard()
            leaderboardData = localLeaderboardData
            if (localLeaderboardData is LeaderboardApiResponses.Success) {
                localLeaderboardData.leaderboard.forEach { id ->
                    val userInfoUseCase = AccountInfoUseCase(id, needCreationDate = false)
                    useCases.add(userInfoUseCase)
                    val player = userInfoUseCase.playerInfo
                    players.add(player)
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
