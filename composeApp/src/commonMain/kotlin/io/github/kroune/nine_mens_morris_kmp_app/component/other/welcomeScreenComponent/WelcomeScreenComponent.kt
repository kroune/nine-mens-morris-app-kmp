package io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.russhwolf.settings.Settings
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountId.AccountIdRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigationToGameWithFriendScreen: () -> Unit,
    private val onNavigationToGameWithBotScreen: () -> Unit,
    private val onNavigationToOnlineGameScreen: () -> Unit,
    private val onNavigationToLeaderboardScreen: () -> Unit,
    private val onNavigationToAccountViewScreen: (accountId: Long) -> Unit,
    private val onNavigationToAuthScreen: () -> Unit,
    private val onNavigationToAboutScreen: () -> Unit,
    private val onNavigationBack: () -> Unit,
    private val accountIdRepository: AccountIdRepositoryI,
    private val jwtTokenRepository: JwtTokenRepositoryI,
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    private val initialStateValue
        get() = WelcomeScreenState(
            null,
            null,
            Settings().getBoolean("hasSeenTutorial", false)
        )

    private val _state = MutableStateFlow(
        initialStateValue
    )
    val state
        get() = _state

    @Volatile
    private var accountCheckingJob: Job? = null
    private val accountCheckingLock = Mutex()

    init {
        doOnStart {
            if (accountCheckingJob?.isActive == true)
                return@doOnStart

            componentScope.launch {
                accountCheckingLock.withLock {
                    if (accountCheckingJob?.isActive == true)
                        return@withLock
                    accountCheckingJob = launch {
                        _state.update {
                            it.copy(
                                isInAccount = jwtTokenRepository.checkJwtToken()
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: WelcomeScreenEvent) {
        fun isInAccount(): Boolean {
            val isInAccountState = state.value.isInAccount
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
                    val accountIdResult = accountIdRepository.getAccountId()
                    if (accountIdResult is AccountIdByJwtTokenApiResponses.Success) {
                        withContext(Dispatchers.Main.immediate) {
                            onNavigationToAccountViewScreen(accountIdResult.accountId)
                        }
                    }
                    _state.update {
                        it.copy(
                            accountIdFailure = accountIdResult
                        )
                    }
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
                Settings().putBoolean("hasSeenTutorial", true)
                _state.update {
                    it.copy(
                        hasSeenTutorial = true
                    )
                }
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

data class WelcomeScreenState(
    val isInAccount: CheckJwtTokenApiResponses?,
    val accountIdFailure: AccountIdByJwtTokenApiResponses?,
    val hasSeenTutorial: Boolean
)