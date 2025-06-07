package io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent

import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.AppStartAnimationScreenEvent

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