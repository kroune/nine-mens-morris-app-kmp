package io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent

import io.github.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CheckJwtTokenApiResponses
import kotlinx.coroutines.flow.StateFlow

interface WelcomeScreenComponentI {
    val isInAccount: StateFlow<CheckJwtTokenApiResponses?>
    fun onEvent(event: WelcomeScreenEvent)
    val accountIdFailure: AccountIdByJwtTokenApiResponses?
    val hasSeenTutorial: Boolean
}