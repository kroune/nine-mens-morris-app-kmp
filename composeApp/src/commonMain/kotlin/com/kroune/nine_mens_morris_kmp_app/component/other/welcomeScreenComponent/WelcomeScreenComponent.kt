package com.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
) : ComponentContext by componentContext, ComponentContextWithBackHandle, WelcomeScreenComponentI {

    private val _accountIdFailure = mutableStateOf<Throwable?>(null)
    override val accountIdFailure by _accountIdFailure

    override val isInAccount = flowOf<Result<Boolean>?>().onStart {
        emit(null)
        emit(jwtTokenInteractor.checkJwtToken())
    }.stateIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.WhileSubscribed(),
        null
    )

    private var _hasSeenTutorial = mutableStateOf(
        Settings().getBoolean("hasSeenTutorial", false)
    )
    override val hasSeenTutorial by _hasSeenTutorial

    override fun onEvent(event: WelcomeScreenEvent) {
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
                _hasSeenTutorial.value = true
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