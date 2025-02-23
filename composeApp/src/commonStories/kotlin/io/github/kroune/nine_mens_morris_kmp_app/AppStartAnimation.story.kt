package kotlin.io.github.kroune.nine_mens_morris_kmp_app

import io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponentI
import io.github.kroune.nine_mens_morris_kmp_app.event.other.AppStartAnimationScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import org.jetbrains.compose.storytale.story

val AppStartAnimation by story {
    object : AppStartAnimationComponentI {
        override fun onEvent(event: AppStartAnimationScreenEvent) {
            TODO("Not yet implemented")
        }
    }.let {
        AppStartAnimationScreen(it)
    }
}