package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.AccountIdByJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.CheckJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ClientErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.NetworkErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ServerErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.UnknownErrorPopUp
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
    private val onNavigationToAccountViewScreen: (accountId: Long) -> Unit
) : ComponentContext by componentContext {

    var isInAccount by mutableStateOf<Boolean?>(null)

    private fun checkJwtToken(): Job {
        val job = CoroutineScope(Dispatchers.Default).launch {
            val checkResult = jwtTokenInteractor.checkJwtToken()
            if (checkResult.isSuccess) {
                isInAccount = checkResult.getOrThrow()
            } else {
                isInAccount = false
                if (checkResult.exceptionOrNull() is CheckJwtTokenApiResponses) {
                    when (checkResult.exceptionOrNull()!!) {
                        is CheckJwtTokenApiResponses.ClientError -> {
                            popupToDraw = @Composable {
                                ClientErrorPopUp()
                            }
                        }

                        is CheckJwtTokenApiResponses.ServerError -> {
                            popupToDraw = @Composable {
                                ServerErrorPopUp()
                            }
                        }

                        is CheckJwtTokenApiResponses.NetworkError -> {
                            popupToDraw = @Composable {
                                NetworkErrorPopUp()
                            }
                        }

                        else -> {
                            popupToDraw = @Composable {
                                UnknownErrorPopUp()
                            }
                        }
                    }
                }
            }
        }
        job.start()
        return job
    }

    private val _checkingJwtTokenJob: MutableStateFlow<Job> = MutableStateFlow(
        Job().apply { this.complete() }
    )

    /**
     * this code updates [_checkingJwtTokenJob] when we switch to another screen
     * this way our [isInAccount] is always valid and we don't need to reload it in any other way
     */
    var checkingJwtTokenJob: StateFlow<Job> =
        MutableStateFlow<Job>(_checkingJwtTokenJob.value).onStart {
            isInAccount = null
            _checkingJwtTokenJob.value = checkJwtToken()
        }.stateIn(
            CoroutineScope(Dispatchers.Default),
            SharingStarted.WhileSubscribed(),
            _checkingJwtTokenJob.value
        )

    var popupToDraw: @Composable () -> Unit by mutableStateOf(
        @Composable {
        }
    )

    var hasSeenTutorial by mutableStateOf(Settings().getBoolean("hasSeenTutorial", false))

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            WelcomeScreenEvent.ClickGameWithFriendButton -> {
                onNavigationToGameWithFriendScreen()
            }

            WelcomeScreenEvent.AccountViewButton -> {
                CoroutineScope(Dispatchers.Default).launch {
                    if (jwtTokenInteractor.getJwtToken() == null) {
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountRegistrationThenViewAccountScreen()
                        }
                        return@launch
                    }
                    val checkResult = accountIdInteractor.getAccountId()
                    if (checkResult.isSuccess) {
                        withContext(Dispatchers.Main) {
                            onNavigationToAccountViewScreen(checkResult.getOrThrow())
                        }
                    } else {
                        if (checkResult.exceptionOrNull() is AccountIdByJwtTokenApiResponses) {
                            when (checkResult.exceptionOrNull()!!) {
                                is AccountIdByJwtTokenApiResponses.ClientError -> {
                                    popupToDraw = @Composable {
                                        ClientErrorPopUp()
                                    }
                                    return@launch
                                }

                                is AccountIdByJwtTokenApiResponses.ServerError -> {
                                    popupToDraw = @Composable {
                                        ServerErrorPopUp()
                                    }
                                    return@launch
                                }

                                is AccountIdByJwtTokenApiResponses.NetworkError -> {
                                    popupToDraw = @Composable {
                                        NetworkErrorPopUp()
                                    }
                                    return@launch
                                }
                            }
                        }
                        popupToDraw = @Composable {
                            UnknownErrorPopUp()
                        }
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
        }
    }

    init {
        backHandler.register(object : BackCallback() {
            override fun onBack() {
                val emptyPopUp: @Composable () -> Unit = @Composable {}
                if (popupToDraw != emptyPopUp) {
                    popupToDraw = emptyPopUp
                } else {
                    this.onBack()
                }
            }

        })
    }
}