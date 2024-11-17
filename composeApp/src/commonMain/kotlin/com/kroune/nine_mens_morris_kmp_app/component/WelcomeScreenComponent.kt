package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.event.WelcomeScreenEvent
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigationToGameWithFriendScreen: () -> Unit,
    private val onNavigationToGameWithBotScreen: () -> Unit,
    private val onNavigationToOnlineGameScreen: () -> Unit,
    private val onNavigationToAccountRegistrationThenViewAccountScreen: () -> Unit,
    private val onNavigationToAccountRegistrationThenOnlineGameScreen: () -> Unit,
    private val onNavigationToAccountViewScreen: (accountId: Long) -> Unit,
    private val onNavigationToAppStartAnimationScreen: () -> Unit
) : ComponentContext by componentContext {

    var isInAccount by mutableStateOf<Result<Boolean>?>(null)
    var accountIdFailure by mutableStateOf<Throwable?>(null)

    private val _checkingJwtTokenJob: MutableStateFlow<Job> = MutableStateFlow(
        Job().apply { this.complete() }
    )

    /**
     * this code updates [_checkingJwtTokenJob] when we switch to another screen
     * this way our [isInAccount] is always valid and we don't need to reload it in any other way
     */
    var checkingJwtTokenJob: StateFlow<Job> =
        MutableStateFlow(_checkingJwtTokenJob.value).onStart {
            isInAccount = null
            _checkingJwtTokenJob.value = CoroutineScope(Dispatchers.Default).launch {
                isInAccount = jwtTokenInteractor.checkJwtToken()
            }
            _checkingJwtTokenJob.value.start()
        }.stateIn(
            CoroutineScope(Dispatchers.Default),
            SharingStarted.WhileSubscribed(),
            _checkingJwtTokenJob.value
        )

    var hasSeenTutorial by mutableStateOf(Settings().getBoolean("hasSeenTutorial", false))

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            WelcomeScreenEvent.ClickGameWithFriendButton -> {
                onNavigationToGameWithFriendScreen()
            }

            WelcomeScreenEvent.AccountViewButton -> {
                CoroutineScope(Dispatchers.Default).launch {
                    _checkingJwtTokenJob.value.join()
                    if (isInAccount?.getOrNull() != true) {
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountRegistrationThenViewAccountScreen()
                        }
                        return@launch
                    }
                    val checkResult = accountIdInteractor.getAccountId()
                    checkResult.onSuccess { accountId ->
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountViewScreen(accountId)
                        }
                    }
                    checkResult.onFailure {
                        accountIdFailure = it
                    }
                }
            }

            WelcomeScreenEvent.ClickGameWithBotButton -> {
                onNavigationToGameWithBotScreen()
            }

            WelcomeScreenEvent.ClickOnlineGameButton -> {
                CoroutineScope(Dispatchers.Default).launch {
                    if (jwtTokenInteractor.getJwtToken() == null) {
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountRegistrationThenOnlineGameScreen()
                        }
                        return@launch
                    }
                    withContext(Dispatchers.Main) {
                        onNavigationToOnlineGameScreen()
                    }
                }
            }

            WelcomeScreenEvent.CloseTutorial -> {
                hasSeenTutorial = true
                Settings().putBoolean("hasSeenTutorial", true)
            }

            WelcomeScreenEvent.RetryGettingAccountId -> {
                isInAccount = null
                _checkingJwtTokenJob.value = CoroutineScope(Dispatchers.Default).launch {
                    isInAccount = jwtTokenInteractor.checkJwtToken()
                }
                _checkingJwtTokenJob.value.start()
            }

            WelcomeScreenEvent.BackToAppStartAnimation -> {
                onNavigationToAppStartAnimationScreen()
            }
        }
    }
}