package com.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent

import com.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import kotlinx.coroutines.flow.StateFlow

interface WelcomeScreenComponentI {
    val isInAccount: StateFlow<Result<Boolean>?>
    fun onEvent(event: WelcomeScreenEvent)
    val accountIdFailure: Throwable?
    val hasSeenTutorial: Boolean
}