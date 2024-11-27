package com.kroune.nine_mens_morris_kmp_app.component

import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.event.AppStartAnimationScreenEvent

class AppStartAnimationComponent(
    componentContext: ComponentContext,
    private val onNavigationToWelcomeScreen: () -> Unit
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    fun onEvent(event: AppStartAnimationScreenEvent) {
        when (event) {
            is AppStartAnimationScreenEvent.ClickButton -> {
                onNavigationToWelcomeScreen()
            }
        }
    }

    override fun onBackPressed() {}
}