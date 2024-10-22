package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.accountInfoRepository
import com.kroune.nine_mens_morris_kmp_app.event.WelcomeScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigationToGameWithFriendScreen: () -> Unit,
    private val onNavigationToGameWithBotScreen: () -> Unit,
    private val onNavigationToOnlineGameScreen: () -> Unit,
    private val onNavigationToAccountViewScreen: () -> Unit
) : ComponentContext by componentContext {
    val isInAccount by mutableStateOf<Boolean?>(null)

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            is WelcomeScreenEvent.ClickGameWithFriendButton -> {
            }

            WelcomeScreenEvent.AccountViewButton -> TODO()
            WelcomeScreenEvent.ClickGameWithBotButton -> TODO()
            WelcomeScreenEvent.ClickOnlineGameButton -> TODO()
        }
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
        }
    }
}