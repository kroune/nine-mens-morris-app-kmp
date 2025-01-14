package com.kroune.nine_mens_morris_kmp_app.component.other

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigationToGameWithFriendScreen: () -> Unit,
    private val onNavigationToGameWithBotScreen: () -> Unit,
    private val onNavigationToOnlineGameScreen: () -> Unit,
    private val onNavigationToLeaderboardScreen: () -> Unit,
    private val onNavigationToAccountViewScreen: (accountId: Long) -> Unit,
    private val onNavigationToAuthScreen: () -> Unit,
    private val onNavigationBack: () -> Unit
) : ComponentContext by componentContext, ComponentContextWithBackHandle {

    private val _accountIdFailure = mutableStateOf<Throwable?>(null)
    val accountIdFailure by _accountIdFailure

    val isInAccount = flowOf<Result<Boolean>?>().onStart {
        emit(null)
        emit(jwtTokenInteractor.checkJwtToken())
    }.stateIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.WhileSubscribed(),
        null
    )

    var hasSeenTutorial by mutableStateOf(Settings().getBoolean("hasSeenTutorial", false))

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            WelcomeScreenEvent.NavigateToGameWithFriend -> {
                onNavigationToGameWithFriendScreen()
            }

            WelcomeScreenEvent.NavigateToAccountView -> {
                if (isInAccount.value?.getOrNull() != true) {
                    // we aren't authorised
                    onNavigationToAuthScreen()
                    return
                }
                CoroutineScope(Dispatchers.Default).launch {
                    accountIdInteractor.getAccountId().fold(
                        onSuccess = { accountId ->
                            withContext(Dispatchers.Main) {
                                onNavigationToAccountViewScreen(accountId)
                            }
                        },
                        onFailure = {
                            _accountIdFailure.value = it
                        }
                    )
                }
            }

            WelcomeScreenEvent.NavigateToGameWithBot -> {
                onNavigationToGameWithBotScreen()
            }

            WelcomeScreenEvent.NavigateToOnlineGame -> {
                if (isInAccount.value?.getOrNull() != true) {
                    // we aren't authorised
                    onNavigationToAuthScreen()
                    return
                }
                onNavigationToOnlineGameScreen()
            }

            WelcomeScreenEvent.CloseTutorial -> {
                hasSeenTutorial = true
                Settings().putBoolean("hasSeenTutorial", true)
            }

            WelcomeScreenEvent.NavigateBack -> {
                onNavigationBack()
            }

            WelcomeScreenEvent.NavigateToLeaderboard -> {
                if (isInAccount.value?.getOrNull() != true) {
                    // we aren't authorised
                    onNavigationToAuthScreen()
                    return
                }
                onNavigationToLeaderboardScreen()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(WelcomeScreenEvent.NavigateBack)
    }
}