package io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.russhwolf.settings.Settings
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
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
    private val onNavigationToAboutScreen: () -> Unit,
    private val onNavigationBack: () -> Unit
) : ComponentContext by componentContext, ComponentContextWithBackHandle, WelcomeScreenComponentI {
    private val componentScope = componentCoroutineScope()

    private val _accountIdFailure = mutableStateOf<AccountIdByJwtTokenApiResponses?>(null)
    override val accountIdFailure by _accountIdFailure

    override val isInAccount = flowOf<CheckJwtTokenApiResponses?>().onStart {
        emit(null)
        emit(jwtTokenInteractor.checkJwtToken())
    }.stateIn(
        componentScope,
        SharingStarted.WhileSubscribed(),
        null
    )

    private var _hasSeenTutorial = mutableStateOf(
        Settings().getBoolean("hasSeenTutorial", false)
    )
    override val hasSeenTutorial by _hasSeenTutorial

    override fun onEvent(event: WelcomeScreenEvent) {
        fun isInAccount(): Boolean {
            val isInAccountState = isInAccount.value
            return isInAccountState is CheckJwtTokenApiResponses.Success && isInAccountState.result
        }
        when (event) {
            WelcomeScreenEvent.NavigateToGameWithFriend -> {
                onNavigationToGameWithFriendScreen()
            }

            WelcomeScreenEvent.NavigateToAccountView -> {
                if (!isInAccount()) {
                    // we aren't authorised
                    onNavigationToAuthScreen()
                    return
                }
                componentScope.launch {
                    val accountIdResult = accountIdInteractor.getAccountId()
                    if (accountIdResult is AccountIdByJwtTokenApiResponses.Success) {
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountViewScreen(accountIdResult.accountId)
                        }
                    }
                    _accountIdFailure.value = accountIdResult
                }
            }

            WelcomeScreenEvent.NavigateToGameWithBot -> {
                onNavigationToGameWithBotScreen()
            }

            WelcomeScreenEvent.NavigateToOnlineGame -> {
                if (!isInAccount()) {
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
                if (!isInAccount()) {
                    // we aren't authorised
                    onNavigationToAuthScreen()
                    return
                }
                onNavigationToLeaderboardScreen()
            }

            WelcomeScreenEvent.NavigateToAboutScreen -> {
                onNavigationToAboutScreen()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(WelcomeScreenEvent.NavigateBack)
    }
}