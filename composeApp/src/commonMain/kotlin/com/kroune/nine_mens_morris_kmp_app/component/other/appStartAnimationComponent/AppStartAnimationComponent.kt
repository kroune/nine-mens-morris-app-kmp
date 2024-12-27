package com.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent

import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.event.other.AppStartAnimationScreenEvent

class AppStartAnimationComponent(
    componentContext: ComponentContext,
    private val onNavigationToWelcomeScreen: () -> Unit
) : ComponentContext by componentContext, ComponentContextWithBackHandle, AppStartAnimationComponentI {
    override fun onEvent(event: AppStartAnimationScreenEvent) {
        when (event) {
            is AppStartAnimationScreenEvent.ClickButton -> {
                onNavigationToWelcomeScreen()
            }
        }
    }

    override fun onBackPressed() {}
}