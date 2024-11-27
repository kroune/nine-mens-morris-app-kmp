package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.event.LeaderboardEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import com.kroune.nine_mens_morris_kmp_app.useCases.AccountInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardComponent(
    val onNavigationBack: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    val players = mutableStateListOf<Player>()
    private val useCases = mutableListOf<AccountInfoUseCase>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val leaderboardData = accountInfoInteractor.getLeaderboard().getOrNull()
            if (leaderboardData == null) {
                players.clear()
                return@launch
            } else {
                leaderboardData.forEach { id ->
                    val userInfoUseCase = AccountInfoUseCase(id, needCreationDate = false)
                    useCases.add(userInfoUseCase)
                    val player = Player(
                        accountName = userInfoUseCase.name,
                        pictureByteArray = userInfoUseCase.accountPicture,
                        accountRating = userInfoUseCase.rating
                    )
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
        }
    }

    override fun onBackPressed() {
        onEvent(LeaderboardEvent.Back)
    }
}

/**
 * holds info about player
 */
data class Player(
    val accountName: State<Result<String>?>,
    val pictureByteArray: State<Result<ByteArray>?>,
    val accountRating: State<Result<Long>?>
)